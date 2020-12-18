package com.example.core.resource;



import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created By hudawei
 * on 2020/12/11 0011
 * 所有需要换肤的Activity都存放在一个集合中
 */
public class SkinActivities {
    private static final List<Entry> sActivities = new ArrayList<>();

    public static void register(String activityClass) {
        sActivities.add(new Entry(activityClass));
    }

    public static boolean isApplySkin(String activityClass) {
        for (Entry entry : sActivities) {
            if (Objects.equals(entry.getActivityClass(), activityClass)) {
                return entry.isApplySkin();
            }
        }
        return false;
    }

    public static void setApplySkin(String activityClass, boolean isApplySkin) {
        Entry target = null;
        for (Entry entry : sActivities) {
            if (Objects.equals(entry.getActivityClass(), activityClass)) {
                target = entry;
                break;
            }
        }
        if (target != null) {
            target.setApplySkin(isApplySkin);
        }
    }


    public static class Entry {
        private final String activityClass;
        private boolean applySkin = true;

        public Entry(String activityClass) {
            this.activityClass = activityClass;
        }

        public String getActivityClass() {
            return activityClass;
        }

        public boolean isApplySkin() {
            return applySkin;
        }

        public void setApplySkin(boolean applySkin) {
            this.applySkin = applySkin;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "activityClass='" + activityClass + '\'' +
                    ", applySkin=" + applySkin +
                    '}';
        }
    }
}
