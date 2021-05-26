package com.example.moog;

import java.net.URLEncoder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

class TranslatedData {
    ResponseData responseData;
}

class ResponseData {
    String translatedText;
}

public class TranslateApi {
    private static final String ENDPOINT = "http://api.mymemory.translated.net";
    public final static String SPANISH = "ES";
    public final static String ENGLISH = "EN";


    private final TranslateService mService;

    public interface TranslateService {
        @GET("/get")
        Call<TranslatedData> getTranslation(
                @Query("q") String textToTranslate,
                @Query(value = "langpair", encoded = true)
                        String languagePair);
    }

    public TranslateApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(TranslateService.class);
    }

    public void translate(final String textToTranslate, final String fromLanguage, final String toLanguage) {
        mService.getTranslation(textToTranslate, URLEncoder.encode(fromLanguage + "|" + toLanguage))
                .enqueue(new Callback<TranslatedData>() {

                    @Override
                    public void onResponse(Call<TranslatedData> call, Response<TranslatedData> response) {
                        String output =
                                String.format("Translation of: %s, %s->%s = %s", textToTranslate,
                                        fromLanguage, toLanguage, response.body().responseData.translatedText);

                        System.out.println("Result: " + output);
                    }

                    @Override
                    public void onFailure(Call<TranslatedData> call, Throwable t) {
                        System.out.println("[DEBUG]" + " RestApi onFailure - " + "");
                    }
                });
    }
}