package com.example.skin_compiler;

import com.example.skin_annotation.Attribute;
import com.example.skin_annotation.SupportSkin;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 该注解器用于处理Attribute注解，生成Skin类
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.Attribute, Constants.SupportSkin})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({"packageName", "isLibrary"})
public class SkinProcessor extends AbstractProcessor {
    private Types mTypeUtils;
    private Elements mElementUtils;
    private List<HandlerEntry> mHandlerEntries;
    private List<String> mSupportSkinActivities;
    private String packageName;
    private String isLibrary;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mHandlerEntries = new ArrayList<>();
        mSupportSkinActivities = new ArrayList<>();

        packageName = processingEnv.getOptions().get("packageName");
        isLibrary = processingEnv.getOptions().get("isLibrary");

        println(packageName + " : " + isLibrary);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            if ("true".equals(isLibrary)) {
                writeLibraryHandlers();
            } else {
                writeSkin();
            }
        } else {
            if (Objects.isNull(isLibrary)) {
                findSupportSkin(roundEnv);
            }
            findAttribute(roundEnv);
        }
        return true;
    }

    /**
     * 用于读取LibraryHandlers中的handlers字段内容
     */
    private List<HandlerEntry> readLibraryHandlers() {
        List<HandlerEntry> handlerEntries = new ArrayList<>();
        TypeElement typeElement = mElementUtils.getTypeElement("com.example.core.LibraryHandlers");
        List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
        String attributes = null;
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.FIELD) {
                VariableElement e = (VariableElement) element;
                attributes = (String) e.getConstantValue();
                break;
            }
        }
        if (attributes != null) {
            String[] array = attributes.split("\n");
            for (String attribute : array) {
                String[] attrs = attribute.split(",");
                handlerEntries.add(new HandlerEntry(attrs[0], attrs[1], attrs[2]));
            }
        }
        return handlerEntries;
    }

    /**
     * 创建LibraryHandlers类，添加handlers字符串字段
     * handlers中存储HandlerEntry信息
     * 格式如下：属性名,方法名,处理类名\n属性名,方法名,处理类名\n
     */
    private void writeLibraryHandlers() {
        StringBuilder sb = new StringBuilder();
        String attrSplit = ",";
        String objSplit = "\n";
        for (HandlerEntry entry : mHandlerEntries) {
            sb.append(entry.attrName).append(attrSplit)
                    .append(entry.methodName).append(attrSplit)
                    .append(entry.handlerClass).append(objSplit);
        }
        if (sb.length() != 0)
            sb.deleteCharAt(sb.length() - 1);
        FieldSpec handlers = FieldSpec.builder(String.class, "handlers", Modifier.PRIVATE, Modifier.FINAL)
                .initializer("$S", sb.toString())
                .build();
        TypeSpec.Builder libraryHandlers = TypeSpec.classBuilder("LibraryHandlers")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(handlers);

        createJavaFile(libraryHandlers);
    }

    /**
     * 创建设置属性方法
     */
    private List<MethodSpec> setXxxMethods() {
        ClassName view = ClassName.get(Constants.PKG_VIEW, Constants.CLS_VIEW);
        ClassName NonNull = ClassName.get(Constants.PKG_ANNOTATION, Constants.CLS_NONNULL);
        //@NonNull View view
        ParameterSpec nonNullView = ParameterSpec.builder(view, "view")
                .addAnnotation(NonNull)
                .build();
        //SkinAttrHandler
        ClassName skinAttrHandler = ClassName.get(Constants.PKG_HANDLER, Constants.CLS_SKIN_ATTR_HANDLER);
        ClassName handlerMap = ClassName.get(Constants.PKG_HANDLER, Constants.CLS_HANDLER_MAP);
        ClassName attributeUtils = ClassName.get(Constants.PKG_HANDLER, Constants.CLS_SKIN_ATTR_HANDLER_UTILS);
        ParameterizedTypeName listSkinAttrHandler = ParameterizedTypeName.get(ClassName.get(List.class), skinAttrHandler);
        List<MethodSpec> setXxxList = new ArrayList<>();
        for (HandlerEntry entry : mHandlerEntries) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(entry.methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addParameter(nonNullView)
                    .addParameter(int.class, "resId")
                    .addStatement("String attributeName = $S", entry.attrName)
                    .addStatement("$T handlers = $T.get(attributeName)", listSkinAttrHandler, handlerMap)
                    .beginControlFlow("for ($T handler : handlers)", skinAttrHandler)
                    .beginControlFlow("if (handler.isSupport(view, attributeName, resId)) ")
                    .addStatement("$T.tryAddSkinView(view, attributeName, resId)", attributeUtils)
                    .addStatement("handler.handle(view, attributeName, resId)")
                    .addStatement("break")
                    .endControlFlow()
                    .endControlFlow();
            JavaDoc.getInstance().write("设置$S属性")
                    .writeEmpty()
                    .write("@param view")
                    .write("@param resId")
                    .args(entry.attrName)
                    .commit(methodBuilder);
            MethodSpec setXxx = methodBuilder.build();
            if (!setXxxList.contains(setXxx))
                setXxxList.add(setXxx);
        }
        return setXxxList;
    }

    /**
     * 创建获取皮肤包存储目录方法
     */
    private MethodSpec getSkinDirMethod() {
        ClassName context = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_CONTEXT);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getSkinDir")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(File.class)
                .addParameter(context, "context")
                .addStatement("$T cacheDir = context.getApplicationContext().getCacheDir()", File.class)
                .addStatement("$T skin = new File(cacheDir, \"skin\")", File.class)
                .beginControlFlow("if (!skin.exists())")
                .addStatement("skin.mkdir()")
                .endControlFlow()
                .addStatement("return skin");
        JavaDoc.getInstance().write("获取皮肤包存储目录")
                .writeEmpty()
                .write("@param context 上下文")
                .write("@return 皮肤包存储目录")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建存储当前使用皮肤包名方法
     */
    private MethodSpec putCurSkinMethod() {
        ClassName context = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_CONTEXT);
        ClassName editor = ClassName.get("android.content.SharedPreferences", "Editor");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("putCurSkin")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(context, "context")
                .addParameter(String.class, "skinName")
                .addStatement("$T edit = context.getSharedPreferences(\"skin\", $T.MODE_PRIVATE).edit()",
                        editor, context)
                .addStatement("edit.putString(\"CUR_SKIN_NAME\", skinName)")
                .addStatement("edit.apply()");

        JavaDoc.getInstance().write("存储当前所使用的皮肤包名")
                .writeEmpty()
                .write("@param context 上下文")
                .write("@param skinName 当前使用的皮肤包名")
                .commit(builder);

        return builder.build();
    }

    /**
     * 创建获取当前使用皮肤包名方法
     */
    private MethodSpec getCurSkinMethod() {
        ClassName context = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_CONTEXT);
        ClassName sharedPreferences = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_SHARED_PREFERENCES);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("getCurSkin")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(String.class)
                .addParameter(context, "context")
                .addStatement("$T sp = context.getSharedPreferences(\"skin\", $T.MODE_PRIVATE)",
                        sharedPreferences, context)
                .addStatement("return sp.getString(\"CUR_SKIN_NAME\", \"\")");
        JavaDoc.getInstance().write("获取当前使用的皮肤包名")
                .writeEmpty()
                .write("@param context 上下文")
                .write("@return 当前使用的皮肤包名")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建应用皮肤包方法
     */
    private MethodSpec applySkinMethod() {
        ClassName context = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_CONTEXT);
        ClassName skinResourcesManager = ClassName.get(Constants.PKG_RESOURCE,
                Constants.CLS_SKIN_RESOURCES_MANAGER);
        ClassName skinViews = ClassName.get(Constants.PKG_ATTR,
                Constants.CLS_SKIN_VIEWS);
        ClassName applySkinListeners = ClassName.get(Constants.PKG_LISTENER,
                Constants.CLS_APPLY_SKIN_LISTENERS);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("applySkin")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(context, "context")
                .addStatement("$T skinName = getCurSkin(context)", String.class)
                .addStatement("$T skinPath = getSkinDir(context) + \"/\" + skinName", String.class)
                .addStatement("$T.getInstance().updateResources(context, skinName, skinPath)",
                        skinResourcesManager)
                .addStatement("$T.getInstance().apply()", skinViews)
                .addStatement("$T.getInstance().apply(skinName)", applySkinListeners);
        JavaDoc.getInstance().write("应用当前皮肤（切换皮肤的时候调用）")
                .writeEmpty()
                .write("@param context 上下文")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建设置LayoutInflater的Factory2方法
     */
    private MethodSpec installViewFactoryMethod() {
        ClassName context = ClassName.get(Constants.PKG_CONTENT, Constants.CLS_CONTEXT);
        ClassName layoutInflater = ClassName.get(Constants.PKG_VIEW, Constants.CLS_LAYOUT_INFLATER);
        ClassName layoutInflaterCompat = ClassName.get("androidx.core.view", "LayoutInflaterCompat");
        ClassName skinFactory = ClassName.get("com.example.core.layout", "SkinFactory");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("installViewFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(context, "context")
                .addStatement("$T layoutInflater = $T.from(context)", layoutInflater, layoutInflater)
                .beginControlFlow("if (layoutInflater.getFactory() == null)")
                .addStatement("$T.setFactory2(layoutInflater, new $T())",
                        layoutInflaterCompat, skinFactory)
                .endControlFlow();
        JavaDoc.getInstance().write("设置布局加载器的Factory2")
                .write("必须在Activity调用super.onCreate()前调用")
                .writeEmpty()
                .write("@param context 上下文")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建setApplySkin方法
     */
    private MethodSpec setApplySkinMethod() {
        ClassName appCompatActivity = ClassName.get("androidx.appcompat.app", "AppCompatActivity");
        ClassName skinActivities = ClassName.get("com.example.core.resource", "SkinActivities");
        MethodSpec.Builder builder = MethodSpec.methodBuilder("setApplySkin")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(appCompatActivity, "activity")
                .addParameter(boolean.class, "isApplySkin")
                .addStatement("$T.setApplySkin(activity.getClass().getCanonicalName(), isApplySkin)",
                        skinActivities);
        JavaDoc.getInstance().write("设置指定Activity是否使用皮肤功能")
                .write("该Activity必须使用@SupportSkin标注")
                .write("必须在Activity调用super.onCreate()前调用")
                .writeEmpty()
                .write("@param activity    指定Activity")
                .write("@param isApplySkin 是否使用皮肤功能")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建注册Handler方法
     */
    private MethodSpec registerHandlersMethod() {
        ClassName handlerMap = ClassName.get(Constants.PKG_HANDLER, Constants.CLS_HANDLER_MAP);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("registerHandlers")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TypeName.VOID);
        for (HandlerEntry entry : mHandlerEntries) {
            int lastPointer = entry.handlerClass.lastIndexOf(".");
            String packageName = entry.handlerClass.substring(0, lastPointer);
            String className = entry.handlerClass.substring(lastPointer + 1);
            ClassName handler = ClassName.get(packageName, className);
            builder.addStatement("$T.register($S, new $T())",
                    handlerMap, entry.attrName, handler);
        }
        JavaDoc.getInstance().write("注册处理皮肤属性类")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建registerActivities方法
     */
    private MethodSpec registerActivitiesMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("registerActivities")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
                .returns(TypeName.VOID);

        ClassName skinActivities = ClassName.get("com.example.core.resource", "SkinActivities");
        for (String activityClass : mSupportSkinActivities) {
            builder.addStatement("$T.register($S)", skinActivities, activityClass);
        }

        JavaDoc.getInstance().write("注册支持皮肤功能的Activity类")
                .write("必须在调用installF")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建初始化方法
     */
    private MethodSpec initMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addStatement("registerHandlers()")
                .addStatement("registerActivities()");
        JavaDoc.getInstance().write("初始化操作")
                .commit(builder);
        return builder.build();
    }

    private MethodSpec addApplySkinListenerMethod() {
        ClassName applySkinListeners = ClassName.get(Constants.PKG_LISTENER, Constants.CLS_APPLY_SKIN_LISTENERS);
        ClassName applySkinListener = ClassName.get(Constants.PKG_LISTENER, Constants.CLS_APPLY_SKIN_LISTENER);
        MethodSpec.Builder builder = MethodSpec.methodBuilder("addApplySkinListener")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(applySkinListener, "listener")
                .returns(TypeName.VOID)
                .addStatement("$T.getInstance().add(listener)", applySkinListeners);
        JavaDoc.getInstance().write("添加皮肤切换监听器，实现皮肤切换时特殊处理")
                .writeEmpty()
                .write("@param listener 监听器")
                .commit(builder);
        return builder.build();
    }

    /**
     * 创建Skin类
     */
    private void writeSkin() {
        mHandlerEntries.addAll(readLibraryHandlers());

        TypeSpec.Builder skinUtils = TypeSpec.classBuilder("Skin")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(initMethod())
                .addMethods(setXxxMethods())
                .addMethod(registerHandlersMethod())
                .addMethod(registerActivitiesMethod())
                .addMethod(getSkinDirMethod())
                .addMethod(putCurSkinMethod())
                .addMethod(getCurSkinMethod())
                .addMethod(applySkinMethod())
                .addMethod(installViewFactoryMethod())
                .addMethod(setApplySkinMethod())
                .addMethod(addApplySkinListenerMethod());

        createJavaFile(skinUtils);
    }

    /**
     * 创建Java文件
     */
    private void createJavaFile(TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder.addJavadoc("Created By hudawei\nwarn:不可修改此类，此类由注解器自动生成\n");
        JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build()).indent("    ").build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在编译阶段打印日志
     */
    private void println(String message) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    /**
     * 寻找使用Attribute注解的类
     */
    private void findAttribute(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Attribute.class);
        for (Element e : elements) {
            if (verifyAttributeClass(e)) {
                TypeElement typeElement = (TypeElement) e;
                Attribute annotation = typeElement.getAnnotation(Attribute.class);
                String attrName = annotation.attrName();
                String methodName = annotation.methodName();
                if (methodName.isEmpty()) {
                    methodName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
                }
                String className = typeElement.getQualifiedName().toString();
                mHandlerEntries.add(new HandlerEntry(attrName, methodName, className));
            }
        }
    }

    /**
     * 寻找SupportSkin标注的Activity类
     */
    private void findSupportSkin(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(SupportSkin.class);
        for (Element e : elements) {
            if (verifySupportSkinClass(e)) {
                TypeElement typeElement = (TypeElement) e;
                String className = typeElement.getQualifiedName().toString();
                mSupportSkinActivities.add(className);
            }
        }
    }

    /**
     * 验证当前使用Attribute的类是否符合要求
     */
    private boolean verifyAttributeClass(Element e) {
        Set<Modifier> modifiers = e.getModifiers();
        boolean isPublic = modifiers.contains(Modifier.PUBLIC);
        boolean isClass = e.getKind() == ElementKind.CLASS;
        boolean isAbstract = modifiers.contains(Modifier.ABSTRACT);
        TypeMirror typeMirror = e.asType();
        TypeMirror baseTypeElement = mElementUtils.getTypeElement("com.example.core.attr.handler.BaseSkinAttrHandler")
                .asType();
        boolean isSubtype = mTypeUtils.isSubtype(typeMirror, baseTypeElement);
        boolean isDefaultConstructor = isDefaultConstructor(e);
        return isPublic && isClass && !isAbstract && isSubtype && isDefaultConstructor;
    }

    /**
     * 验证当前使用SupportSkin的类是否符合要求
     */
    private boolean verifySupportSkinClass(Element e) {
        Set<Modifier> modifiers = e.getModifiers();
        boolean isPublic = modifiers.contains(Modifier.PUBLIC);
        boolean isClass = e.getKind() == ElementKind.CLASS;
        boolean isAbstract = modifiers.contains(Modifier.ABSTRACT);
        TypeMirror typeMirror = e.asType();
        TypeMirror baseTypeElement = mElementUtils.getTypeElement("androidx.appcompat.app.AppCompatActivity")
                .asType();
        boolean isSubtype = mTypeUtils.isSubtype(typeMirror, baseTypeElement);
        return isPublic && isClass && !isAbstract && isSubtype;
    }

    /**
     * 类中是否有默认构造方法
     */
    private boolean isDefaultConstructor(Element e) {
        List<? extends Element> enclosedElements = e.getEnclosedElements();
        for (Element element : enclosedElements) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement executableElement = (ExecutableElement) element;
                if (executableElement.getParameters().size() == 0) {
                    return executableElement.getModifiers().contains(Modifier.PUBLIC);
                }
            }
        }
        return false;
    }
}