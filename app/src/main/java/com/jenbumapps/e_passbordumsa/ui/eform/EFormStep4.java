package com.jenbumapps.e_passbordumsa.ui.eform;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jenbumapps.core.api.ApiManager;
import com.jenbumapps.core.model.City;
import com.jenbumapps.core.model.time.DateHelper;
import com.jenbumapps.core.model.time.LocalDateTime;
import com.jenbumapps.e_passbordumsa.R;
import com.jenbumapps.e_passbordumsa.app.App;
import com.jenbumapps.e_passbordumsa.utility.pico.Pico;
import com.jenbumapps.e_passbordumsa.utility.pico.codec.Type;
import com.jenbumapps.e_passbordumsa.utility.pico.helper.PicoListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jenbumapps.e_passbordumsa.app.App.ePass;

/**
 * ROUTE AND JOURNEY DETAILS
 */
public class EFormStep4 extends Fragment implements View.OnClickListener {
    private static final String TAG = EFormStep4.class.getSimpleName();
    private FragmentActivity mContext;

    private LinearLayout llContainerHeader;

    // Navigation
    private TextView tvPrev;
    private TextView tvNext;

    // Journey details
    private TextView tvDateOfJourney;
    private EditText etVehicleRc;
    private TextView tvDriver;
    private EditText etDriverContact;
    private EditText etRouteOfJourney;
    private Spinner spinCity;
    private ArrayAdapter<City> cityArrayAdapter;
    private List<City> cities = new ArrayList<>();

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
        View view = inflater.inflate(R.layout.fragment_e_form_step4, container, false);

        initView(view);
        initStatusBar();
        initAdapter();
        initListener();

        checkData();
        fetchCities();

        return view;
    }

    private void fetchCities() {
        Dialog dialog = new SpotsDialog.Builder()
                .setContext(mContext)
                .setMessage("Fetching cities ..")
                .build();
        dialog.show();
        ApiManager.city().fetchActive().enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                dialog.dismiss();
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        cities.clear();

                        City city = new City();
                        city.setActive(true);
                        city.setId(-1);
                        city.setName("Select your area Authority");
                        cities.add(city);

                        cities.addAll(response.body());
                        cityArrayAdapter.notifyDataSetChanged();

                        if(ePass.getCity() != null){
                            for(City c : cities) {
                                if(c.getId() == ePass.getCity().getId()) {
                                    spinCity.setSelection(cityArrayAdapter.getPosition(c));
                                    return;
                                }
                            }
                        }

                    } else {
                        Toast.makeText(mContext, "No active cities found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, "Error fetching active cities", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                Toast.makeText(mContext, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initAdapter() {
        cityArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, cities);
        cityArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCity.setAdapter(cityArrayAdapter);
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
        // Journey details
        tvDateOfJourney = view.findViewById(R.id.tv_date_of_journey);
        etVehicleRc = view.findViewById(R.id.et_vehicle_rc);
        tvDriver = view.findViewById(R.id.tv_driver);
        etDriverContact = view.findViewById(R.id.et_driver_phone);
        etRouteOfJourney = view.findViewById(R.id.et_route);
        spinCity = view.findViewById(R.id.spin_city);

        // Navigation
        tvPrev = view.findViewById(R.id.tv_prev);
        tvNext = view.findViewById(R.id.tv_next);
    }


    private void initListener() {
        tvDateOfJourney.setOnClickListener(this);
        tvPrev.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        spinCity.setOnItemSelectedListener(citySelectionListener());
    }

    private AdapterView.OnItemSelectedListener citySelectionListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(((City) parent.getSelectedItem()).getId() == -1){
                    Toast.makeText(mContext, "Select your authority", Toast.LENGTH_SHORT).show();
                    return;
                }
                ePass.setCity((City) parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    private void checkData() {
        if(ePass.getDateOfJourney() != null) {
            String doj = DateHelper.formatDate(ePass.getDateOfJourney())+ " "
                    +DateHelper.formatTime(ePass.getDateOfJourney());
            tvDateOfJourney.setText(doj);
        }

        if(!TextUtils.isEmpty(ePass.getVehicleRcNumber())) {
            etVehicleRc.setText(ePass.getVehicleRcNumber());
        }

        if(!TextUtils.isEmpty(ePass.getDriverName())) {
            tvDriver.setText(ePass.getDriverName());
        }

        if(ePass.getDriverContact() != 0) {
            etDriverContact.setText(String.valueOf(ePass.getDriverContact()));
        }

        if(!TextUtils.isEmpty(ePass.getRouteOfJourney())) {
            etRouteOfJourney.setText(ePass.getRouteOfJourney());
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_date_of_journey:
                showDatePicker();
                break;

            case R.id.tv_prev:
                gotoPrevStep();
                break;

            case R.id.tv_next:
                gotoNextStep();
                break;
        }

    }

    private void gotoPrevStep() {
        if(etVehicleRc.getText().toString().trim().length()>5) {
            ePass.setVehicleRcNumber(etVehicleRc.getText().toString().trim());
        }

        if(etDriverContact.getText().toString().trim().length() == 10) {
            ePass.setDriverContact(Long.parseLong(etDriverContact.getText().toString().trim()));
        }

        if(etRouteOfJourney.getText().toString().trim().length() >5) {
            ePass.setRouteOfJourney(etRouteOfJourney.getText().toString().trim());
        }

        showFragment(new EFormStep3());
    }

    private void gotoNextStep() {

        if(etVehicleRc.getText().toString().trim().length()>5) {
            ePass.setVehicleRcNumber(etVehicleRc.getText().toString().trim());
        } else {
            Toast.makeText(mContext, "Enter a valid vehicle rc number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(etDriverContact.getText().toString().trim().length() == 10) {
            ePass.setDriverContact(Long.parseLong(etDriverContact.getText().toString().trim()));
        } else {
            Toast.makeText(mContext, "Enter a valid driver contact number", Toast.LENGTH_SHORT).show();
            return;
        }

        if(etRouteOfJourney.getText().toString().trim().length() >5) {
            ePass.setRouteOfJourney(etRouteOfJourney.getText().toString().trim());
        } else {
            Toast.makeText(mContext, "Enter full route details", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(ePass.getDateOfJourney() == null) {
            Toast.makeText(mContext, "Select the date of journey", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ePass.getCity() == null) {
            Toast.makeText(mContext, "Select your area authority", Toast.LENGTH_SHORT).show();
            return;
        }

        showFragment(new EFormStep5());
    }

    private void showFragment(Fragment frag) {
        FragmentManager fragmentManager = mContext.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        fragmentTransaction.replace(R.id.container_frag, frag);
        if(frag instanceof EFormStep5) {
            fragmentTransaction.addToBackStack(EFormStep4.class.getSimpleName());
        } else {
            mContext.getSupportFragmentManager().popBackStack();
        }
        fragmentTransaction.commit();
    }

    private void showDatePicker() {
        Pico pico = new Pico(mContext, "Date of Journey", Type.CALENDAR);
        pico.setPicoListener(new PicoListener() {
            @Override
            public void result(Calendar calendar) {
                showTimePicker(calendar);
            }
        });
        pico.show();
    }

    private void showTimePicker(Calendar date) {
        Pico pico = new Pico(mContext, "Time of Journey", Type.TIME);
        pico.setPicoListener(new PicoListener() {
            @Override
            public void result(Calendar calendar) {

                calendar.set(Calendar.YEAR, date.get(Calendar.YEAR));
                calendar.set(Calendar.MONTH, date.get(Calendar.MONTH));
                calendar.set(Calendar.DATE, date.get(Calendar.DATE));

                ePass.setDateOfJourney(LocalDateTime.of(calendar));
                tvDateOfJourney.setText(Pico.formatDateTime(calendar));
            }
        });
        pico.show();
    }
}
