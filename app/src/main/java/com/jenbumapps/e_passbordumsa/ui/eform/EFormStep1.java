package com.jenbumapps.e_passbordumsa.ui.eform;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jenbumapps.core.model.codec.FormType;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;
import com.jenbumapps.e_passbordumsa.ui.SearchActivity;

/**
 * FORM TYPE SELECTOR
 */
public class EFormStep1 extends Fragment implements View.OnClickListener {
    private static final String TAG = EFormStep1.class.getSimpleName();
    private FragmentActivity mContext;
    private CardView cvMedical;
    private CardView cvEssentialGoods;
    private CardView cvTeaArecanut;
    private CardView cvConstructionMaterial;
    private CardView cvLabourStudent;
    private CardView cvIntraArunachal;
    private ImageView ivSearch;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FragmentActivity) {
            this.mContext = (FragmentActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e_form_step1, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.WHITE);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.accent_white));
            }

        }

        initView(view);
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        FragmentManager manager = mContext.getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void initView(View view) {
        ivSearch = view.findViewById(R.id.iv_search);
        cvMedical = view.findViewById(R.id.cv_medical);
        cvEssentialGoods = view.findViewById(R.id.cv_essential_goods);
        cvTeaArecanut = view.findViewById(R.id.cv_tea_arecanut);
        cvConstructionMaterial = view.findViewById(R.id.cv_construction_material);
        cvLabourStudent = view.findViewById(R.id.cv_labour_student);
        cvIntraArunachal = view.findViewById(R.id.cv_intra_arunachal_pass);
    }

    private void initListener() {
        ivSearch.setOnClickListener(this);
        cvMedical.setOnClickListener(this);
        cvEssentialGoods.setOnClickListener(this);
        cvTeaArecanut.setOnClickListener(this);
        cvConstructionMaterial.setOnClickListener(this);
        cvLabourStudent.setOnClickListener(this);
        cvIntraArunachal.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_search:
                startActivity(new Intent(mContext, SearchActivity.class));
                return;
            case R.id.cv_medical:
                App.ePass.setFormType(FormType.MEDICAL);
                break;

            case R.id.cv_essential_goods:
                App.ePass.setFormType(FormType.ESSENTIAL_GOODS);
                break;

            case R.id.cv_tea_arecanut:
                App.ePass.setFormType(FormType.TEA_ARECA_NUT);
                break;

            case R.id.cv_construction_material:
                App.ePass.setFormType(FormType.CONSTRUCTION_MATERIAL);
                break;

            case R.id.cv_labour_student:
                App.ePass.setFormType(FormType.LABOUR_STUDENT);
                break;

            case R.id.cv_intra_arunachal_pass:
                App.ePass.setFormType(FormType.INTRA_ARUNACHAL_PASS);
                break;
        }

        showFragment();

    }

    private void showFragment() {

        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, new EFormStep2());
        fragmentTransaction.addToBackStack(EFormStep1.class.getSimpleName());
        fragmentTransaction.commit();
    }
}
