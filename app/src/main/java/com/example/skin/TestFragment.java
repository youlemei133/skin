package com.example.skin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Created By hudawei
 * on 2020/12/8 0008
 */
public class TestFragment extends Fragment {
    public static TestFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        View view1 = view.findViewById(R.id.view1);
        View view2 = view.findViewById(R.id.view2);
        View view3 = view.findViewById(R.id.view3);
        View view4 = view.findViewById(R.id.view4);

        Skin.setBackground(view1, R.color.purple_200);
        Skin.setBackground(view2, R.color.testColor);
        Skin.setBackground(view3, R.color.purple_700);
        Skin.setBackground(view4, R.color.teal_200);

        TextView textView = view.findViewById(R.id.textView);
        textView.setText(getArguments().getInt("position") + "");
        Skin.setTextColor(textView,R.color.teal_200);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity();
    }
}
