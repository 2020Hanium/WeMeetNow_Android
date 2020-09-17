package hanium.android.wemeetnow.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import hanium.android.MyApplication;
import hanium.android.wemeetnow.R;
import io.socket.emitter.Emitter;

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
        JSONObject obj = new JSONObject();
        try {
            obj.put("userEmail", j_id.getText().toString());
            obj.put("userPwd", j_pw.getText().toString());
            obj.put("userName", j_name.getText().toString());

            MyApplication.socket.emit("add_user", obj);
            MyApplication.socket.on("success_add_user", onSuccess);
            MyApplication.socket.on("fail_add_user", onFail);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Emitter.Listener onSuccess = args -> {
        Log.d("socket", "Join Success");
        runOnUiThread(() -> {
            finish();
            Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
        });
    };

    Emitter.Listener onFail = args -> {
        Log.d("socket", "Join Fail");
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show());
    };

}