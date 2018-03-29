package com.example.shang.filemanager.Fragment;

import android.support.v4.app.Fragment;

/**
 * Created by Shang on 2017/4/1.
 */
public class FragmentFactory {

    public static Fragment getFragment(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new FirstFragment();
        } else if (position == 1) {
            fragment = new SecondFragment();
        } else if (position == 2) {
            fragment = new ThirdFragment();
        } else if (position == 3) {
            fragment = new FourFragment();
        } else if (position == 4) {
            fragment = new FiveFragment();
        } else if (position == 5) {
            fragment = new SixFragment();
        }
        return fragment;
    }
}
