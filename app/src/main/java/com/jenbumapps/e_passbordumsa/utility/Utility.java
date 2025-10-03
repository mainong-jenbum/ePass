package com.jenbumapps.e_passbordumsa.utility;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.jenbumapps.core.api.ApiManager;
import com.jenbumapps.e_passbordumsa.R;

import java.io.File;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    public static void addNextFragment(FragmentActivity activity, Fragment toFrag, Fragment fromFrag, boolean slideVertical) {
        FragmentTransaction beginTransaction = activity.getSupportFragmentManager().beginTransaction();
        if (slideVertical) {
            beginTransaction.setCustomAnimations(R.animator.slide_fragment_vertical_right_in,
                    R.animator.slide_fragment_vertical_left_out,
                    R.animator.slide_fragment_vertical_left_in,
                    R.animator.slide_fragment_vertical_right_out);
        } else {
            beginTransaction.setCustomAnimations(R.animator.slide_fragment_horizontal_right_in,
                    R.animator.slide_fragment_horizontal_left_out,
                    R.animator.slide_fragment_horizontal_left_in,
                    R.animator.slide_fragment_horizontal_right_out);
        }

        beginTransaction.add(R.id.container_frag, toFrag, toFrag.getClass().getSimpleName());
        beginTransaction.hide(fromFrag);
        beginTransaction.addToBackStack(toFrag.getClass().getSimpleName());
        beginTransaction.commit();
    }

    public static void addNextFragmentFadeAnim(FragmentActivity activity, Fragment fromFrag, Fragment toFrag) {
        FragmentTransaction beginTransaction = activity.getSupportFragmentManager().beginTransaction();
        beginTransaction.setCustomAnimations(R.animator.enter, R.animator.exit, R.animator.pop_enter, R.animator.pop_exit);
        beginTransaction.replace(R.id.container_frag, toFrag, toFrag.getClass().getSimpleName());

//        beginTransaction.hide(fromFrag == null? new HomeFragment() : fromFrag);
        beginTransaction.addToBackStack(toFrag.getClass().getSimpleName());
        beginTransaction.commit();
    }


    private static long exitTime = 0;
    public static void exitApp(Activity context){

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(context, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            context.finish();
        }
    }


    /********************************* FILE UPLOAD **********************************************/

    public interface ImageUploadListener{
        void onSuccess(String url);
        void onFailure(String reason);
    }
    public static void uploadFile(FragmentActivity mContext, File file, ImageUploadListener listener, boolean isImage) {

        Dialog dialog = new SpotsDialog.Builder()
                .setContext(mContext)
                .setMessage("Uploading file..")
                .build();
        dialog.show();
        MultipartBody.Part filePart;
        if(isImage) {
            filePart = MultipartBody.Part.createFormData("file",file.getName() , RequestBody.create(file, MediaType.parse("image/*")));
        } else {
            filePart = MultipartBody.Part.createFormData("file",file.getName() , RequestBody.create(file, MediaType.parse("application/*")));
        }

        ApiManager.file().saveImage(filePart).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dialog.dismiss();
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        listener.onSuccess(response.body());
                    } else {
                        listener.onFailure("No url returned");
                    }
                } else {
                    listener.onFailure("Error code :"+response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
                listener.onFailure("Server error");
            }
        });

    }
}
