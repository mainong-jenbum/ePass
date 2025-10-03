package com.jenbumapps.e_passbordumsa.ui.eform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.model.DocumentDetail;
import com.jenbumapps.core.model.File;
import com.jenbumapps.core.model.codec.DocType;
import com.jenbumapps.core.model.codec.FileType;
import com.jenbumapps.core.model.codec.FormType;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;
import com.jenbumapps.e_passbordumsa.ui.adapter.FileAdapter;
import com.jenbumapps.e_passbordumsa.utility.ImageCompression;
import com.jenbumapps.e_passbordumsa.utility.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;

/**
 * DOCUMENT UPLOAD
 */
public class EFormStep5 extends Fragment implements View.OnClickListener {
    private static final String TAG = EFormStep5.class.getSimpleName();
    private FragmentActivity mContext;
    private LinearLayout llContainerHeader;
    private TextView tvPrev;
    private TextView tvNext;

    // Supporting documents
    private TextView tvApplicationDoc;
    private TextView tvMedicalLabel;
    private TextView tvMedicalDoc;
    private TextView tvRcDoc;
    private TextView tvOtherDoc;
    private RecyclerView rvUpload;
    private FileAdapter fileAdapter;
    private List<File> files = new ArrayList<>();

    private DocumentDetail documentDetail = new DocumentDetail();
    private DocType docType;
    private EasyImage easyImage;

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
        View view = inflater.inflate(R.layout.fragment_e_form_step5, container, false);

        initView(view);
        initStatusBar();
        initAdapter();
        initListener();

        checkData();

        return view;
    }

    private void initView(View view) {
        llContainerHeader = view.findViewById(R.id.ll_container_header);
        tvPrev = view.findViewById(R.id.tv_prev);
        tvNext = view.findViewById(R.id.tv_next);

        // Uploading documents
        tvApplicationDoc = view.findViewById(R.id.tv_application);
        tvMedicalLabel = view.findViewById(R.id.tv_medical_label);
        tvMedicalDoc = view.findViewById(R.id.tv_medical);
        tvRcDoc = view.findViewById(R.id.tv_vehicle_rc);
        tvOtherDoc = view.findViewById(R.id.tv_other_doc);
        rvUpload = view.findViewById(R.id.rv_upload);
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

    private void initAdapter() {

        // File Adapter
        fileAdapter = new FileAdapter(files, fileListener(), true);
        rvUpload.setAdapter(fileAdapter);
        rvUpload.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private FileAdapter.Listener fileListener() {
        return new FileAdapter.Listener() {
            @Override
            public void onFileDeleteClicked(com.jenbumapps.core.model.File file, int pos) {

                files.remove(pos);
                fileAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFileSelected(com.jenbumapps.core.model.File file, int pos) {

            }
        };
    }
    private void initListener() {
        tvNext.setOnClickListener(this);
        tvPrev.setOnClickListener(this);
        tvApplicationDoc.setOnClickListener(this);
        tvRcDoc.setOnClickListener(this);
        tvMedicalDoc.setOnClickListener(this);
        tvOtherDoc.setOnClickListener(this);

    }

    private void checkData() {

        if(App.ePass.getFormType() != FormType.MEDICAL) {
            tvMedicalDoc.setVisibility(View.GONE);
            tvMedicalLabel.setVisibility(View.GONE);
        }

        if(App.ePass.getDocumentDetail() != null){
            documentDetail = App.ePass.getDocumentDetail();

            if(documentDetail.getApplication() != null) {
                tvApplicationDoc.setText("Application uploaded");
                for (Drawable drawable : tvApplicationDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvApplicationDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }

            if(documentDetail.getVehicleRc() != null) {
                tvRcDoc.setText("Vehicle RC uploaded");

                for (Drawable drawable : tvApplicationDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvRcDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }

            if(documentDetail.getOtherSupportingDocuments() != null && documentDetail.getOtherSupportingDocuments().size()>0) {
                tvOtherDoc.setText(String.format(Locale.ENGLISH,"%d Supporting document uploaded", documentDetail.getOtherSupportingDocuments().size()));

                for (Drawable drawable : tvApplicationDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvOtherDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }

            if(App.ePass.getFormType() != FormType.MEDICAL) {
                return;
            }
            if(documentDetail.getMedicalDocument() != null) {
                tvMedicalDoc.setText("Medical document uploaded");

                for (Drawable drawable : tvApplicationDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvMedicalDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
            }
        }
    }

    private void showFragment(Fragment frag) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, frag);
        if(frag instanceof EFormStep6) {
            fragmentTransaction.addToBackStack(EFormStep5.class.getSimpleName());
        } else {
            mContext.getSupportFragmentManager().popBackStack();
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                gotoNextStep();
                break;

            case R.id.tv_prev:
                gotoPrevStep();
                break;

            case R.id.tv_application:
                docType = DocType.APPLICATION;
                showFileChooser();
                break;

            case R.id.tv_medical:
                docType = DocType.MEDICAL_DOCUMENT;
                showFileChooser();
                break;

            case R.id.tv_vehicle_rc:
                docType = DocType.VEHICLE_RC;
                showFileChooser();
                break;

            case R.id.tv_other_doc:
                docType = DocType.OTHER;
                showFileChooser();
                break;
        }
    }

    private void showFileChooser() {
        easyImage = new EasyImage.Builder(mContext).build();
        easyImage.openChooser(this);
    }

    private void gotoPrevStep() {
        App.ePass.setDocumentDetail(documentDetail);
        showFragment(new EFormStep4());
    }

    private void gotoNextStep() {
        if(documentDetail.getApplication() == null) {
            Toast.makeText(mContext, "Upload application", Toast.LENGTH_SHORT).show();
            return;
        }

        if(documentDetail.getVehicleRc() == null) {
            Toast.makeText(mContext, "Upload vehicle RC", Toast.LENGTH_SHORT).show();
            return;
        }

        if(files.size()>0) {
            documentDetail.setOtherSupportingDocuments(files);
        }

        if(App.ePass.getFormType() == FormType.MEDICAL) {
            if(documentDetail.getMedicalDocument() == null) {
                Toast.makeText(mContext, "Upload medical document", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        App.ePass.setDocumentDetail(documentDetail);
        showFragment(new EFormStep6());
    }

    private void setDocument(String url, DocType type) {
        switch (type) {
            case APPLICATION:
                File application = new File();
                application.setUrl(url);
                application.setType(FileType.IMAGE);
                application.setDocType(type);
                documentDetail.setApplication(application);
                tvApplicationDoc.setText("Uploaded");

                for (Drawable drawable : tvApplicationDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvApplicationDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                break;
            case MEDICAL_DOCUMENT:
                File medical = new File();
                medical.setUrl(url);
                medical.setType(FileType.IMAGE);
                medical.setDocType(type);
                documentDetail.setMedicalDocument(medical);
                tvMedicalDoc.setText("Uploaded");

                for (Drawable drawable : tvMedicalDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvMedicalDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                break;
            case VEHICLE_RC:
                File rc = new File();
                rc.setUrl(url);
                rc.setType(FileType.IMAGE);
                rc.setDocType(type);
                documentDetail.setVehicleRc(rc);
                tvRcDoc.setText("Uploaded");

                for (Drawable drawable : tvRcDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvRcDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                break;
            case OTHER:
                File oth = new File();
                oth.setUrl(url);
                oth.setType(FileType.IMAGE);
                oth.setDocType(type);
                files.add(oth);
                documentDetail.setOtherSupportingDocuments(files);
                tvOtherDoc.setText("Uploaded");

                for (Drawable drawable : tvOtherDoc.getCompoundDrawables()) {
                    if (drawable != null) {
                        drawable.clearColorFilter();
                    }
                }
                tvOtherDoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                break;
        }
        docType = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        easyImage.handleActivityResult(requestCode, resultCode, data, mContext, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {

                if(imageFiles.length == 0) return;

                java.io.File file = new java.io.File(ImageCompression.compressImage(imageFiles[0].getFile().getAbsolutePath(), mContext));

                Utility.uploadFile(mContext, file, new Utility.ImageUploadListener() {
                    @Override
                    public void onSuccess(String url) {
                        setDocument(url, docType);
                    }

                    @Override
                    public void onFailure(String reason) {
                        Toast.makeText(mContext, reason, Toast.LENGTH_SHORT).show();
                    }
                }, true);

            }

            @Override
            public void onImagePickerError(@NonNull Throwable error, @NonNull MediaSource source) {
                //Some error handling
                error.printStackTrace();
            }

            @Override
            public void onCanceled(@NonNull MediaSource source) {
                //Not necessary to remove any files manually anymore
            }
        });
    }
}
