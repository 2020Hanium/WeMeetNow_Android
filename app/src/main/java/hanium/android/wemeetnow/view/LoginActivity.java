package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import hanium.android.MyApplication;
import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.model.LoginModel;
import hanium.android.wemeetnow.model.SuccessResponse;
import hanium.android.wemeetnow.network.RetrofitInstance;
import hanium.android.wemeetnow.util.PreferenceManager;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id, et_pw;
    private Button btn_login, btn_join;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    private void initialize() {
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(onClickListener);
        btn_join = findViewById(R.id.btn_join);
        btn_join.setOnClickListener(onClickListener);
    }

    private void requestLogin() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", et_id.getText().toString());
            obj.put("userPwd", et_pw.getText().toString());

            MyApplication.socket.emit("login", obj);
            MyApplication.socket.on("login_info", onLogin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Emitter.Listener onLogin = args -> {
//        saveUserInfo(et_id.getText().toString(), et_pw.getText().toString(), message.substring(8, 11));
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        JSONObject obj = (JSONObject)args[0];
        try {
            String name = obj.getString("name");
            Log.d("socket", "Login: " + name);
            saveUserInfo(et_id.getText().toString(), et_pw.getText().toString(), name);
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Login: " + name, Toast.LENGTH_SHORT).show());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

//    private void requestLogin() {
//        RetrofitInstance.getInstance().getService().userLogin(new LoginModel(et_id.getText().toString(), et_pw.getText().toString())).enqueue(new Callback<SuccessResponse>() {
//            @Override
//            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
//                if (response.isSuccessful()) {
//                    SuccessResponse successResponse = response.body();
//                    if (successResponse != null) {
//                        int code = successResponse.code;
//                        String message = successResponse.message;
//                        if (code == 200) {
//                            saveUserInfo(et_id.getText().toString(), et_pw.getText().toString(), message.substring(8, 11));
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//                            Log.d("LoginActivity",  code + " " + message);
//                        }
//                    } else {
//                        Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                        Log.d("LoginActivity",  response.code() + " " + response.message());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SuccessResponse> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                Log.d("LoginActivity",  t.getMessage());
//            }
//        });
//    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_login:{
                requestLogin();
                break;
            }
            case R.id.btn_join:{
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
            }
        }
    };

    private void saveUserInfo(String id, String pw, String name){
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.ID, id);
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.PASSWORD, pw);
        PreferenceManager.getInstance().putSharedPreference(getApplicationContext(), Constant.Preference.NAME, name);
        Log.d("LoginActivity", "ID: " + id + ", PASSWORD: " + pw + ", NAME: " + name);
    }
}
