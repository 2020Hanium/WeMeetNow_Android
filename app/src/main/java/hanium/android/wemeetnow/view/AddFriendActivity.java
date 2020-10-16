package hanium.android.wemeetnow.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import hanium.android.wemeetnow.MyApplication;
import hanium.android.wemeetnow.R;
import io.socket.emitter.Emitter;

public class AddFriendActivity extends AppCompatActivity {

    private EditText et_id;
    private TextView tv_name;
    private Button btn_ac, btn_cc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initialize();
    }

    private void initialize() {
        et_id = findViewById(R.id.et_id);

        AppCompatImageButton btn_IDsearch = findViewById(R.id.btn_IDsearch);
        btn_IDsearch.setOnClickListener(onClickListener);

        tv_name = findViewById(R.id.tv_name);

        btn_ac = findViewById(R.id.btn_ac);
        btn_ac.setOnClickListener(onClickListener);
        btn_cc = findViewById(R.id.btn_cc);
        btn_cc.setOnClickListener(onClickListener);
    }

    private void searchID() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("friendEmail", et_id.getText().toString());
            MyApplication.socket.emit("add_friend", obj);

            MyApplication.socket.on("ok_add_friend", onSuccessSearching);
            MyApplication.socket.on("false_add_friend", onFailSearching);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addFriendToServer() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("friendEmail", et_id.getText().toString());
            MyApplication.socket.emit("accept_friend", obj);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_IDsearch:{
                searchID();
                break;
            }
            case R.id.btn_ac:{
                addFriendToServer();
                break;
            }
            case R.id.btn_cc:{
                onBackPressed();
                break;
            }
        }
    };

    Emitter.Listener onSuccessSearching = args -> {
        JSONObject obj = (JSONObject)args[0];
        try {
            String id = obj.getString("id");
            Log.d("socket", "Searching Success: " + id);
            runOnUiThread(() -> {
                tv_name.setText(id + "");
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

    Emitter.Listener onFailSearching = args -> {
        Log.d("socket", "Searching Fail");
        runOnUiThread(() -> {
            Toast.makeText(getApplicationContext(), "존재하지 않는 회원입니다.", Toast.LENGTH_SHORT).show();
        });
    };

}
