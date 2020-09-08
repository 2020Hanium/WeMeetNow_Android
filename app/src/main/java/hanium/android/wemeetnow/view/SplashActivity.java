package hanium.android.wemeetnow.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.model.LoginModel;
import hanium.android.wemeetnow.model.SuccessResponse;
import hanium.android.wemeetnow.network.RetrofitInstance;
import hanium.android.wemeetnow.util.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private String id, pw, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler handler = new Handler();
//        if (checkFirstLogin()) {
            handler.postDelayed(this::goToLogin, 1600);
//        } else {
//            requestLogin();
//        }
    }

    private boolean checkFirstLogin() {
        id = PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.ID, null);
        pw = PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.PASSWORD, null);
        name = PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.NAME, null);

        return id == null || pw == null || name == null;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

//    private void requestLogin(){
//        RetrofitInstance.getInstance().getService().userLogin(new LoginModel(id, pw)).enqueue(new Callback<SuccessResponse>() {
//            @Override
//            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
//                if (response.isSuccessful()) {
//                    SuccessResponse successResponse = response.body();
//                    if (successResponse != null) {
//                        int code = successResponse.code;
//                        String message = successResponse.message;
//                        if (code == 200) {
//                            name = message.substring(8, 11);
//                            saveUserInfo();
//                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
////                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                            Log.d("SplashActivity",  code + " " + message);
//                            goToLogin();
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                        Log.d("SplashActivity",  response.code() + " " + response.message());
//                        goToLogin();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SuccessResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                Log.d("SplashActivity",  t.getMessage());
//                goToLogin();
//            }
//        });
//    }

    private void requestLogin(){

    }

    private void saveUserInfo(){
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.ID, id);
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.PASSWORD, pw);
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.NAME, name);
        Log.d("SplashActivity", "ID: " + id + ", PASSWORD: " + pw + ", NAME: " + name);
    }
}