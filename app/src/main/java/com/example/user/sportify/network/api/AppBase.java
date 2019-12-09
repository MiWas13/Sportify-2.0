package com.example.user.sportify.network.api;

import android.app.Application;
import android.content.Context;

import com.example.user.sportify.network.services.BaseService;
import com.yandex.mapkit.MapKitFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.user.sportify.ui.utils.Constants.MAPKIT_API_KEY;

public class AppBase extends Application {

    private static BaseService baseService;
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();


    @Override
    public void onCreate() {
        super.onCreate();

        MapKitFactory.setApiKey(MAPKIT_API_KEY);

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okClient = new OkHttpClient.Builder()
//                .addInterceptor(new ResponseInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://wasser7i.bget.ru/sportify/v2/")
                .client(okClient)
                .build();
        baseService = retrofit.create(BaseService.class);


    }

    public static BaseService getBaseService() {
        return baseService;
    }


//    public static Retrofit getClient(Context context) {
//        if (retrofit == null) {
//            retrofit = new Retrofit.Builder()
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl("http://wasser7i.bget.ru/sportify/v2/")
//                    .build();
//        }
//        return retrofit;
//    }
}