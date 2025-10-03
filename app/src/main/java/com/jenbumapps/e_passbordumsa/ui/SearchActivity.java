package com.jenbumapps.e_passbordumsa.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jenbumapps.core.api.ApiManager;
import com.jenbumapps.core.model.EPass;
import com.jenbumapps.core.model.codec.ApproveStatus;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.ui.adapter.PermitAdapter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, PermitAdapter.Listener {
    private EditText etSearch;
    private Button btnSearch;

    private RecyclerView rvPermit;
    private PermitAdapter permitAdapter;

    private List<EPass> permits = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initAdapter();
        initListener();
    }

    private void initView() {

        etSearch = findViewById(R.id.et_phone);
        btnSearch = findViewById(R.id.btn_search);
        rvPermit = findViewById(R.id.rv_permit);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.holo_red_light));
            }

        }

    }
    private void initAdapter() {

        permitAdapter = new PermitAdapter(permits, this);
        rvPermit.setAdapter(permitAdapter);
        rvPermit.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initListener() {
        btnSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_search) {
            if(validationSuccess()) {
                search();
            }
        }
    }

    private boolean validationSuccess() {
        if(etSearch.getText().length() != 10){
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void search() {

        Dialog progressDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Searching..")
                .setCancelable(true)
                .build();

        progressDialog.show();

        ApiManager.form().fetchByApplicantContact(Long.parseLong(etSearch.getText().toString().trim()))
                .enqueue(new Callback<List<EPass>>() {
                    @Override
                    public void onResponse(Call<List<EPass>> call, Response<List<EPass>> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()) {
                            if(response.body() != null) {
                                permits.clear();
                                permits.addAll(response.body());
                                permitAdapter.notifyDataSetChanged();
                                if(permits.size()>0) {
                                    rvPermit.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if(response.code() == 404) {
                                permits.clear();
                                permitAdapter.notifyDataSetChanged();
                                Toast.makeText(SearchActivity.this, "We couldn't find any request with this phone number. Try again!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SearchActivity.this, "Server error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<EPass>> call, Throwable t) {
                        progressDialog.dismiss();
                        t.printStackTrace();
                        Toast.makeText(SearchActivity.this, "Server error!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onPermitSelected(EPass formData, int pos, View view) {
        if(formData.getStatus() == ApproveStatus.PENDING){
            Toast.makeText(this, "Your request has not been approved yet. Check again later!", Toast.LENGTH_SHORT).show();
            return;
        } else if(formData.getStatus() == ApproveStatus.REJECTED) {
            Toast.makeText(this, "Your request has been rejected!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PermitDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("PERMIT", Parcels.wrap(formData));
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
