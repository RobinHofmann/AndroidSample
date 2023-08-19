package com.example.androidsample;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ExchangeRateAPI {

    @GET("USD")
    Call<ExchangeRateResponse> getExchangeRates();
}
