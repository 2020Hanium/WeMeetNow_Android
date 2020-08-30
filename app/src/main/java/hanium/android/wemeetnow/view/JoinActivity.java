package hanium.android.wemeetnow.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.model.JoinModel;
import hanium.android.wemeetnow.model.SuccessResponse;
import hanium.android.wemeetnow.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {

    private EditText j_name, j_id, j_pw;
    private Button j_ac, j_cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        initialize();
    }

    private void initialize() {
        j_name = findViewById(R.id.j_name);
        j_id = findViewById(R.id.j_id);
        j_pw = findViewById(R.id.j_pw);
        j_ac = findViewById(R.id.j_ac);
        j_ac.setOnClickListener(onClickListener);
        j_cc = findViewById(R.id.j_cc);
        j_cc.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.j_ac: {
                if (j_name.getText().length() == 0 || j_id.getText().length() == 0 || j_pw.getText().length() == 0){
                    Toast.makeText(getApplicationContext(), "빈 칸을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendInfo();
                }
                break;
            }
            case R.id.j_cc: {
                onBackPressed();
                break;
            }
        }
    };

    private void sendInfo() {
        RetrofitInstance.getInstance().getService().userJoin(new JoinModel(j_name.getText().toString(), j_id.getText().toString(), j_pw.getText().toString())).enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                if (response.isSuccessful()) {
                    SuccessResponse successResponse = response.body();
                    if (successResponse != null) {
                        int code = successResponse.code;
                        String message = successResponse.message;
                        if (code == 200) {
                            finish();
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            Log.d("JoinActivity",  code + " " + message);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        Log.d("JoinActivity",  response.code() + " " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                Log.d("JoinActivity",  t.getMessage());
            }
        });
    }
}
