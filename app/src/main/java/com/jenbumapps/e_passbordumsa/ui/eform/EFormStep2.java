package com.jenbumapps.e_passbordumsa.ui.eform;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jenbumapps.core.model.ApplicantDetail;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;

/**
 * APPLICANT DETAILS
 */
public class EFormStep2 extends Fragment implements View.OnClickListener {
    private static final String TAG = EFormStep2.class.getSimpleName();
    private FragmentActivity mContext;

    // Applicant details
    private EditText etApplicantName;
    private EditText etApplicantContact;
    private EditText etApplicantAddress;
    private TextView tvNext;
    private TextView tvPrev;
    private LinearLayout llContainerHeader;

    // Variable
    private ApplicantDetail applicantDetail;

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
        View view = inflater.inflate(R.layout.fragment_e_form_step2, container, false);


        initView(view);
        initStatusBar();
        initListener();

        checkData();

        return view;
    }

    private void initStatusBar() {

        int color;

        switch (App.ePass.getFormType()) {
            default:
            case MEDICAL:
                color = ContextCompat.getColor(mContext, R.color.colorMedical);
                break;
            case ESSENTIAL_GOODS:
                color = ContextCompat.getColor(mContext, R.color.colorEssentialGoods);
                break;
            case TEA_ARECA_NUT:
                color = ContextCompat.getColor(mContext, R.color.colorTeaArecanut);
                break;
            case CONSTRUCTION_MATERIAL:
                color = ContextCompat.getColor(mContext, R.color.colorConstructionMaterial);
                break;
            case LABOUR_STUDENT:
                color = ContextCompat.getColor(mContext, R.color.colorLabourStudent);
                break;
            case INTRA_ARUNACHAL_PASS:
                color = ContextCompat.getColor(mContext, R.color.colorIntraArunachal);
                break;
        }
        llContainerHeader.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mContext.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(mContext, R.color.accent_white));
            }

        }
    }

    private void initView(View view) {

        llContainerHeader = view.findViewById(R.id.ll_container_header);

        // Applicant details
        etApplicantName = view.findViewById(R.id.et_applicant_name);
        etApplicantContact = view.findViewById(R.id.et_applicant_contact);
        etApplicantAddress = view.findViewById(R.id.et_applicant_address);
        tvPrev = view.findViewById(R.id.tv_prev);
        tvNext = view.findViewById(R.id.tv_next);
    }

    private void initListener() {
        tvPrev.setOnClickListener(this);
        tvNext.setOnClickListener(this);

    }

    /**
     * Check if data exists for this step
     * fill the views if data exists
     */
    private void checkData() {
        applicantDetail = App.ePass.getApplicantDetail();

        if(applicantDetail == null) return;

        etApplicantName.setText(applicantDetail.getName());
        etApplicantContact.setText(String.valueOf(applicantDetail.getContact()));
        etApplicantAddress.setText(applicantDetail.getAddress());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_prev:
                gotoPrevStep();
                break;

            case R.id.tv_next:
                gotoNextStep();
                break;
        }
    }

    /**
     * Save existing data
     * goto step1
     */
    private void gotoNextStep() {
        applicantDetail = new ApplicantDetail();
        if(etApplicantName.getText().toString().trim().length()>2) {
            applicantDetail.setName(etApplicantName.getText().toString().trim());
        } else {
            Toast.makeText(mContext, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(etApplicantContact.getText().toString().trim().length() == 10){
            applicantDetail.setContact(Long.parseLong(etApplicantContact.getText().toString().trim()));
        } else {
            Toast.makeText(mContext, "Enter a valid contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(etApplicantAddress.getText().toString().trim().length() > 5) {
            applicantDetail.setAddress(etApplicantAddress.getText().toString());
        } else {
            Toast.makeText(mContext, "Enter a valid address", Toast.LENGTH_SHORT).show();
            return;
        }

        App.ePass.setApplicantDetail(applicantDetail);
        showFragment(new EFormStep3());
    }

    /**
     * validate fields
     * Save existing data
     * goto step2
     */
    private void gotoPrevStep() {
        applicantDetail = new ApplicantDetail();
        if(!TextUtils.isEmpty(etApplicantName.getText().toString().trim())) {
            applicantDetail.setName(etApplicantName.getText().toString().trim());
        }

        if(etApplicantContact.getText().toString().trim().length() == 10){
            applicantDetail.setContact(Long.parseLong(etApplicantContact.getText().toString().trim()));
        }

        if(!TextUtils.isEmpty(etApplicantAddress.getText().toString().trim())) {
            applicantDetail.setAddress(etApplicantAddress.getText().toString());
        }

        App.ePass.setApplicantDetail(applicantDetail);
        showFragment(new EFormStep1());
    }

    private void showFragment(Fragment frag) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, frag);

        if(frag instanceof EFormStep3) {
            fragmentTransaction.addToBackStack(EFormStep2.class.getSimpleName());
        } else {
            mContext.getSupportFragmentManager().popBackStack();
        }

        fragmentTransaction.commit();
    }
}
