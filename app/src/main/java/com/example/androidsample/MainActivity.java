package com.example.androidsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    private AutoCompleteTextView actvLeft;
    private AutoCompleteTextView actvRight;

    private Button button;
    private ArrayList<String> exchangeRates;
    Map<String, Double> conversionRates;
    private String url = "https://v6.exchangerate-api.com/v6/" + ApiKeys.getExchangeRateKey() + "/latest/";



    public MainActivity() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView textViewResult = findViewById(R.id.textViewResult);
        Button button = (Button) findViewById(R.id.button);


        // BUTTON ---------------
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                //Log.d("BUTTONS", "User tapped the Button");
                convertCurrency(v);
            }
        });

        // GET Request -----------
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://v6.exchangerate-api.com/v6/dcf307f391433e5e3fd1aab9/latest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ExchangeRateAPI exchangeRateAPI = retrofit.create(ExchangeRateAPI.class);

        Call<ExchangeRateResponse> call = exchangeRateAPI.getExchangeRates();
        call.enqueue(new Callback<ExchangeRateResponse>() {
            @Override
            public void onResponse(Call<ExchangeRateResponse> call, Response<ExchangeRateResponse> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("code: " + response.code());
                    return;
                }

                // if successful
                ExchangeRateResponse exchangeRateResponse = response.body();
                conversionRates = exchangeRateResponse.getConversionRates();

                exchangeRates = new ArrayList<String>(conversionRates.keySet());

                ArrayAdapter<String> adapterLeft = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.select_dialog_item, exchangeRates);

                ArrayAdapter<String> adapterRight = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.select_dialog_item, exchangeRates);

                actvLeft = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewLeft);
                actvRight = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewRight);


                actvLeft.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            actvLeft.showDropDown();
                        }
                    }
                });
                actvRight.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            actvRight.showDropDown();
                        }
                    }
                });

                actvLeft.setThreshold(0);//will start working from first character
                actvLeft.setAdapter(adapterLeft);//setting the adapter data into the AutoCompleteTextView

                actvRight.setThreshold(0);//will start working from first character
                actvRight.setAdapter(adapterRight);//setting the adapter data into the AutoCompleteTextView

            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }


    public void convertCurrency(View view) {
        EditText inputText = findViewById(R.id.inputText);
        TextView textViewResult = findViewById(R.id.textViewResult);

        if (!inputText.getText().toString().equals("")){

            float baseValue = Float.parseFloat(inputText.getText().toString()); //value to convert

            String baseCurrency = actvLeft.getText().toString();
            String targetCurrency = actvRight.getText().toString();

            float baseExchangeRate = conversionRates.get(baseCurrency).floatValue();
            float targetExchangeRate = conversionRates.get(targetCurrency).floatValue();

            float targetValue = (baseValue/baseExchangeRate)*targetExchangeRate;

            textViewResult.setText(String.format(Locale.ENGLISH,"%.2f %s", targetValue, targetCurrency));
        } else {
            textViewResult.setText(R.string.no_value_string);
        }
    }
}