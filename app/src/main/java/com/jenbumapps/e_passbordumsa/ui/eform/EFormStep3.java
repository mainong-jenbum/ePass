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
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.model.Traveller;
import com.jenbumapps.core.model.codec.FormType;
import com.jenbumapps.core.model.codec.TravellerType;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;
import com.jenbumapps.e_passbordumsa.ui.adapter.TravellerAdapter;
import com.jenbumapps.e_passbordumsa.utility.ImageCompression;
import com.jenbumapps.e_passbordumsa.utility.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


/**
 * TRAVELLER DETAILS
 */
public class EFormStep3 extends Fragment implements TravellerAdapter.Listener, View.OnClickListener {
    private static final String TAG = EFormStep3.class.getSimpleName();
    private FragmentActivity mContext;

    private LinearLayout llContainerHeader;

    // Traveller details
    private EditText etTravellerName;
    private Spinner spinTravellerType;
    private TextView tvAddNewTraveller;
    private TextView tvIdProof;
    private TextView tvSaveTraveller;
    private TextView tvPrev;
    private TextView tvNext;
    private CardView cvAddNewTravellerContainer;
    private RecyclerView rvTravellers;

    private TravellerAdapter travellerAdapter;

    private Traveller traveller = new Traveller();
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
        View view = inflater.inflate(R.layout.fragment_e_form_step3, container, false);

        initView(view);
        initStatusBar();
        initAdapter();
        initListener();

        checkData();

        return view;
    }
    private void initStatusBar() {

        int color;
        if(App.ePass.getFormType() == null) return;

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
        // Traveller details
        etTravellerName = view.findViewById(R.id.et_traveller_name);
        spinTravellerType = view.findViewById(R.id.spin_traveller_type);
        tvIdProof = view.findViewById(R.id.tv_id_proof);
        cvAddNewTravellerContainer = view.findViewById(R.id.cv_passenger_container);
        tvAddNewTraveller = view.findViewById(R.id.tv_add_new_traveller);
        tvSaveTraveller = view.findViewById(R.id.tv_save_traveller);
        tvPrev = view.findViewById(R.id.tv_prev);
        tvNext = view.findViewById(R.id.tv_next);
        rvTravellers = view.findViewById(R.id.rv_traveller);
    }

    private void initAdapter() {
        travellerAdapter = new TravellerAdapter(App.travellers, this, true);
        rvTravellers.setAdapter(travellerAdapter);
        rvTravellers.setLayoutManager(new LinearLayoutManager(mContext));

        List<TravellerType> travellerTypes = new ArrayList<>();
        travellerTypes.add(TravellerType.SELECT_TRAVELLER_TYPE);
        travellerTypes.add(TravellerType.DRIVER);
        travellerTypes.add(TravellerType.PASSENGER);
        travellerTypes.add(TravellerType.HANDYMAN);
        travellerTypes.add(TravellerType.ATTENDANT);
        travellerTypes.add(TravellerType.PATIENT);
        ArrayAdapter<TravellerType> travellerTypeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, travellerTypes);
        travellerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTravellerType.setAdapter(travellerTypeAdapter);
    }

    private void initListener() {
        tvAddNewTraveller.setOnClickListener(this);
        tvIdProof.setOnClickListener(this);
        tvSaveTraveller.setOnClickListener(this);
        tvPrev.setOnClickListener(this);
        tvNext.setOnClickListener(this);
    }

    private void checkData() {

        tvAddNewTraveller.setVisibility(View.VISIBLE);
        cvAddNewTravellerContainer.setVisibility(View.GONE);
    }

    @Override
    public void onTravellerDelete(Traveller traveller, int pos) {
        App.travellers.remove(pos);
        travellerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTravellerSelected(Traveller traveller, int pos) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add_new_traveller:
                cvAddNewTravellerContainer.setVisibility(View.VISIBLE);
                tvAddNewTraveller.setVisibility(View.GONE);
                break;

            case R.id.tv_id_proof:
                easyImage = new EasyImage.Builder(mContext).build();
                easyImage.openChooser(this);
                break;


            case R.id.tv_save_traveller:
                saveTraveller();
                break;

            case R.id.tv_prev:
                gotoPreviousStep();
                break;

            case R.id.tv_next:
                gotoNextStep();
                break;

        }
    }

    private void gotoPreviousStep() {
        showFragment(new EFormStep2());
    }

    private void gotoNextStep() {
        if(App.travellers.size()==0) {
            Toast.makeText(mContext, "Enter traveller details", Toast.LENGTH_SHORT).show();
        } else {
            boolean driver = false;
            for(Traveller t : App.travellers) {
                if (t.getType() == TravellerType.DRIVER) {
                    driver = true;
                    break;
                }
            }
            
            if(!driver) {
                Toast.makeText(mContext, "Add a driver detail", Toast.LENGTH_SHORT).show();
                return;
            }

            showFragment(new EFormStep4());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        easyImage.handleActivityResult(requestCode, resultCode, data, mContext, new DefaultCallback() {
            @Override
            public void onMediaFilesPicked(MediaFile[] imageFiles, MediaSource source) {

                if(imageFiles.length == 0) return;
                File file = new File(ImageCompression.compressImage(imageFiles[0].getFile().getAbsolutePath(), mContext));
                Utility.uploadFile(mContext, file, new Utility.ImageUploadListener() {
                    @Override
                    public void onSuccess(String url) {
                        com.jenbumapps.core.model.File file = new com.jenbumapps.core.model.File();
                        file.setUrl(url);
                        traveller.setIdProof(url);
                        for (Drawable drawable : tvIdProof.getCompoundDrawables()) {
                            if (drawable != null) {
                                drawable.clearColorFilter();
                            }
                        }
                        tvIdProof.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
                        tvIdProof.setText("UPLOADED");
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

    private void saveTraveller() {
        if(etTravellerName.getText().length() <3) {
            Toast.makeText(mContext, "Traveller Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(spinTravellerType.getSelectedItem() == TravellerType.SELECT_TRAVELLER_TYPE) {
            Toast.makeText(mContext, "Select traveller type", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!URLUtil.isValidUrl(traveller.getIdProof())){
            Toast.makeText(mContext, "Upload id proof", Toast.LENGTH_SHORT).show();
            return;
        }

        traveller.setName(etTravellerName.getText().toString().trim());
        traveller.setType((TravellerType) spinTravellerType.getSelectedItem());

        App.travellers.add(traveller);
        travellerAdapter.notifyDataSetChanged();

        if(traveller.getType() == TravellerType.DRIVER){
            App.ePass.setDriverName(traveller.getName());
        }

        traveller = new Traveller();
        etTravellerName.setText("");
        spinTravellerType.setSelection(0);

        cvAddNewTravellerContainer.setVisibility(View.GONE);
        tvAddNewTraveller.setVisibility(View.VISIBLE);

        tvIdProof.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cloud_upload, 0, 0, 0);
        for (Drawable drawable : tvIdProof.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(tvIdProof.getContext(), android.R.color.darker_gray), PorterDuff.Mode.SRC_IN));
            }
        }
        tvIdProof.setText("ID PROOF");

    }


    private void showFragment(Fragment frag) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, frag);
        if(frag instanceof EFormStep4) {
            fragmentTransaction.addToBackStack(EFormStep3.class.getSimpleName());
        } else {
            mContext.getSupportFragmentManager().popBackStack();
        }
        fragmentTransaction.commit();
    }
}
