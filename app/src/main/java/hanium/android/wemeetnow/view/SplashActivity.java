package hanium.android.wemeetnow.view;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.util.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private String id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Handler handler = new Handler();
        if (checkFirstLogin()) {
            handler.postDelayed(this::goToLogin, 1600);
        } else {
            requestLogin(id, pw);
        }
    }

    private boolean checkFirstLogin() {
        id = PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.ID, null);
        pw = PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.PASSWORD, null);

        return id == null || pw == null;
    }

    private void goToLogin() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
        finish();
    }

    private void requestLogin(String id, String pw){
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("passwd", pw);


    }

    private void saveUserInfo(String id, String pw){
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.ID, id);
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.PASSWORD, pw);
        Log.d("SplashActivity", "ID: " + id + ", PASSWORD: " + pw);
    }
}