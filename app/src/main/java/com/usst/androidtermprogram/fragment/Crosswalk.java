package com.usst.androidtermprogram.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usst.androidtermprogram.R;

import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

public class Crosswalk extends Fragment {

    public Crosswalk() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crosswalk, container, false);
        XWalkView xWalkView = view.findViewById(R.id.xWalkWebView);
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
        String url = "https://www.baidu.com";
        xWalkView.load(url, null);
        return view;
    }
}
