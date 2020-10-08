package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hanium.android.MyApplication;
import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.adapter.FriendsPartyAdapter;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.util.PreferenceManager;
import io.socket.emitter.Emitter;

public class AddPartyActivity extends AppCompatActivity implements FriendsPartyAdapter.OnSelectListener {

    private boolean isClicked = false;
    private List<String> friendList = new ArrayList<>();
    private List<String> showList = new ArrayList<>();
    private Calendar calendar;

    private FriendsPartyAdapter adapter;

    private EditText et_partyname;
    private TextView btn_invite;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_party);

        initialize();
        setRecyclerView();
        setTimePicker();

        MyApplication.socket.emit("refresh_friend");
        MyApplication.socket.on("friend_list", onFriendListReceived);
    }

    @Override
    public void onSelect(String name, boolean add) {
        if (add) {
            showList.add(name);
        }
        else {
            showList.remove(name);
        }

        StringBuilder string = new StringBuilder();
        for(String s : showList) {
            string.append(s).append(" ");
        }
        btn_invite.setText(string.toString());
    }

    private void initialize() {
        et_partyname = findViewById(R.id.et_partyname);

        btn_invite = findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(onClickListener);

        Button btn_access = findViewById(R.id.btn_access);
        btn_access.setOnClickListener(onClickListener);
        Button btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(onClickListener);
    }

    private void setRecyclerView() {
        recyclerView = findViewById(R.id.rv_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendsPartyAdapter(friendList, this);
        recyclerView.setAdapter(adapter);
    }

    private void setTimePicker() {
        calendar = Calendar.getInstance();
        TimePicker picker =  findViewById(R.id.timepicker);

        picker.setHour(calendar.get((Calendar.HOUR_OF_DAY)));
        picker.setMinute(calendar.get((Calendar.MINUTE)));

        picker.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0));
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_invite:{
                clickInvite();
                break;
            }
            case R.id.btn_access:{
                sendPartyInfo();
                break;
            }
            case R.id.btn_cancel:{
                onBackPressed();
                break;
            }
        }
    };

    private void sendPartyInfo() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("party_name", et_partyname.getText().toString());

            Date date = new Date(calendar.getTimeInMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d("socket", "date: " + sdf.format(date));
            obj.put("party_time", sdf.format(date));

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < showList.size(); i++){
                jsonArray.put(showList.get(i));
            }
            Log.d("socket", "showList: " + jsonArray.toString());
            obj.put("members", jsonArray);

            MyApplication.socket.emit("open_party", obj);
            MyApplication.socket.on("ok_partyhead", onPartySuccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clickInvite() {
        if (isClicked) {
            recyclerView.setVisibility(View.GONE);
            isClicked = false;
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            isClicked = true;
        }
    }

    Emitter.Listener onFriendListReceived = args -> {

        Log.d("socket", "Party Friend: " + args[0] + "");
        JSONArray arr = (JSONArray) args[0];
        for (int i = 0; i < arr.length(); i++) {
            try {
                friendList.add(arr.getJSONObject(i).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    };

    Emitter.Listener onPartySuccess = args -> {
        JSONObject obj = (JSONObject)args[0];
        try {
            int totalCount = obj.getInt("total_partyCount");
            Log.d("socket", "Party Success: " + totalCount);
            runOnUiThread(() -> {
                Intent intent = new Intent(AddPartyActivity.this, SetMyLocationActivity.class);
                intent.putExtra("totalCount", totalCount);
                intent.putExtra("head", PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.NAME, null));
                intent.putExtra("partyName", et_partyname.getText().toString());
                startActivity(intent);
                finish();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

}
