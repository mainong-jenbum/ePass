package com.jenbumapps.core.api;

import com.jenbumapps.core.model.EPassTerm;
import com.jenbumapps.core.model.Terms;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface EPassTermApi {

    @GET("/terms-conditions/all")
    Call<List<Terms>> fetchAll();

    @GET("/terms-conditions/form-type/{type}")
    Call<List<Terms>> fetchByFormType(@Path("type") int type);

}