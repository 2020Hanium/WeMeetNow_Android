package hanium.android.wemeetnow.network;

import hanium.android.MyApplication;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static RetrofitInstance instance = null;

    public static RetrofitInstance getInstance() {
        if (instance == null) {
            instance = new RetrofitInstance();
        }
        return instance;
    }

    private RetrofitInstance() {

    }

    public RetrofitService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyApplication.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitService.class);
    }
}

