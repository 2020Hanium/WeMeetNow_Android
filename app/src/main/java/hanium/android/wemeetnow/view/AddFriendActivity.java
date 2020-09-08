package hanium.android.wemeetnow.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import hanium.android.wemeetnow.R;

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

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_IDsearch:{

                break;
            }
            case R.id.btn_ac:{
                finish();
                break;
            }
            case R.id.btn_cc:{
                onBackPressed();
                break;
            }
        }
    };

}
