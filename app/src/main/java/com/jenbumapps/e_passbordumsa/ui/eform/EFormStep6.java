package com.jenbumapps.e_passbordumsa.ui.eform;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.api.ApiManager;
import com.jenbumapps.core.model.EPass;
import com.jenbumapps.core.model.Terms;
import com.jenbumapps.core.model.codec.FormType;
import com.jenbumapps.core.model.time.DateHelper;
import com.jenbumapps.core.model.time.LocalDateTime;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;
import com.jenbumapps.e_passbordumsa.ui.adapter.TCAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jenbumapps.e_passbordumsa.app.App.ePass;

/**
 * TERMS AND CONDITIONS
 * SUBMIT REQUEST
 */
public class EFormStep6 extends Fragment implements View.OnClickListener {
    private static final String TAG = EFormStep6.class.getSimpleName();
    private FragmentActivity mContext;
    private LinearLayout llContainerHeader;

    private TextView tvPrev;
    private TextView tvNext;

    // T&C
    private RecyclerView rvTerms;
    private TCAdapter tcAdapter;
    private List<Terms> terms = new ArrayList<>();
    private CheckBox cbTc;

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
        View view = inflater.inflate(R.layout.fragment_e_form_step6, container, false);

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
        rvTerms = view.findViewById(R.id.rv_t_c);
        cbTc = view.findViewById(R.id.check_t_c);
    }

    private void initAdapter() {
        tcAdapter = new TCAdapter(terms);
        rvTerms.setAdapter(tcAdapter);
        rvTerms.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void initListener() {
        tvPrev.setOnClickListener(this);
        tvNext.setOnClickListener(this);
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

    private void checkData() {
        fetchTerms(ePass.getFormType());
    }

    private void fetchTerms(FormType type) {
        Dialog dialog = new SpotsDialog.Builder()
                .setContext(mContext)
                .setCancelable(true)
                .setMessage("Fetching terms and conditions..")
                .build();
        dialog.show();

        ApiManager.term().fetchByFormType(type.getCode()).enqueue(new Callback<List<Terms>>() {
            @Override
            public void onResponse(Call<List<Terms>> call, Response<List<Terms>> response) {
                dialog.dismiss();
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        terms.clear();
                        terms.addAll(response.body());
                        rvTerms.setVisibility(View.VISIBLE);
                        tcAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error code : "+response.code());
                    }
                } else {
                    Log.d(TAG, "Error code : "+response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Terms>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(mContext, "Server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequest() {
        Dialog progressDialog = new SpotsDialog.Builder()
                .setContext(mContext)
                .setCancelable(true)
                .setMessage("Sending request. Please wait..")
                .build();

        progressDialog.show();

        ePass.setTravellers(App.travellers);
        ApiManager.form().create(ePass).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()) {

                    ePass = new EPass();
                    App.travellers.clear();

                    Toast.makeText(mContext, "Your request has been submitted", Toast.LENGTH_SHORT).show();
                    showFragment(new EFormStep1());
                } else {
                    Toast.makeText(mContext, "Error submitting request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Toast.makeText(mContext, "Server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFragment(Fragment frag) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, frag);
        if(frag instanceof EFormStep5) {
            mContext.getSupportFragmentManager().popBackStack();
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_prev:
                showFragment(new EFormStep5());
                break;

            case R.id.tv_next:
                if(cbTc.isChecked()) {
                    Calendar expiry = ePass.getDateOfJourney().getDate().getDate();
                    expiry.set(Calendar.HOUR_OF_DAY, ePass.getDateOfJourney().getTime().getHour());
                    expiry.set(Calendar.MINUTE, ePass.getDateOfJourney().getTime().getMinute());
                    expiry.add(Calendar.HOUR_OF_DAY, 8);
                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
                            .setTitle("Info")
                            .setMessage("Your permit will expire at "+ DateHelper.formatDate(LocalDateTime.of(expiry)) +" "+ DateHelper.formatTime(LocalDateTime.of(expiry)))

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendRequest();
                                    dialog.dismiss();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.mipmap.ic_launcher);

                    dialog.show();

                } else {
                    Toast.makeText(mContext, "Please accept the terms & conditions first", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
}
