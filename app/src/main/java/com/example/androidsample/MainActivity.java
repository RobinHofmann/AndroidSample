package com.example.androidsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;

    private String url = "https://v6.exchangerate-api.com/v6/" + ApiKeys.getExchangeRateKey() + "/latest/";
    public MainActivity() throws IOException {
    }

    /*private static class HTTPReqTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("https://v6.exchangerate-api.com/v6/dcf307f391433e5e3fd1aab9/latest/USD");
                urlConnection = (HttpURLConnection) url.openConnection();

                int code = urlConnection.getResponseCode();
                if (code !=  200) {
                    throw new IOException("Invalid response from server: " + code);
                }

                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    Log.i("data", line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textViewResult = findViewById(R.id.textView);

        //new HTTPReqTask().execute();



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

                Map<String, Double> conversionRates;
                ExchangeRateResponse posts = response.body();
                conversionRates = posts.getConversionRates();
                double usdExchangeRate = conversionRates.get("EUR");
                textViewResult.setText("USD Conversion Rate: " + usdExchangeRate);
            }

            @Override
            public void onFailure(Call<ExchangeRateResponse> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    /*public static void getExchangeRate() throws Exception{
        URL url = new URL("https://v6.exchangerate-api.com/v6/dcf307f391433e5e3fd1aab9/latest/USD");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET"); //optional as GET is standard
        con.setRequestProperty("User-Agent", "MyApp/1.0");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code : " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream())
            );
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println(response.toString());
        } else {
            System.out.println("GET request did not work");
        }

    }*/


    public void convertCurrency(View view) {
        EditText dollarText = findViewById(R.id.dollarText);


        /*try {
            getExchangeRate();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }*/



        if (!dollarText.getText().toString().equals("")){
            float dollarValue = Float.parseFloat(dollarText.getText().toString());
            float euroValue = dollarValue * 0.85F;
            textViewResult.setText(String.format(Locale.ENGLISH,"%.2f", euroValue));
        } else {
            textViewResult.setText(R.string.no_value_string);
        }
    }
}