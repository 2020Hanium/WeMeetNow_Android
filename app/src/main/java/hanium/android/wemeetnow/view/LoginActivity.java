package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.model.LoginModel;
import hanium.android.wemeetnow.model.LoginResponse;
import hanium.android.wemeetnow.network.RetrofitInstance;
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

    private void sendInfo() {
        RetrofitInstance.getInstance().getService().userLogin(new LoginModel(et_id.getText().toString(), et_pw.getText().toString())).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        int code = loginResponse.code;
                        String message = loginResponse.message;
                        if (code == 200) {
                            Log.d("LoginActivity",  loginResponse.userId + "");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                            Log.d("LoginActivity",  code + " " + message);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity",  response.code() + " " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity",  t.getMessage());
            }
        });
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_login:{
                sendInfo();
                break;
            }
            case R.id.btn_join:{
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
            }
        }
    };
}
