package com.jenbumapps.e_passbordumsa.app;


import android.app.Application;

import com.jenbumapps.core.model.EPass;
import com.jenbumapps.core.model.Traveller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App extends Application {

    public static Map<String, String> globalParams;
    public static EPass ePass = new EPass();
    public static List<Traveller> travellers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
