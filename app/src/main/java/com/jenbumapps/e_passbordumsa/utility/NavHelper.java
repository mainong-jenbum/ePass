package com.jenbumapps.e_passbordumsa.utility;

import androidx.fragment.app.Fragment;

public interface NavHelper {
    default void showFragment(Fragment fragment){};
    default void showFragment(Fragment parent, Fragment child){};
}
