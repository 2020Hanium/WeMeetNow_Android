package hanium.android.wemeetnow.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import hanium.android.MyApplication;
import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.adapter.FriendsPartyAdapter;
import io.socket.emitter.Emitter;

public class AddPartyActivity extends AppCompatActivity implements FriendsPartyAdapter.OnSelectListener {

    private boolean isClicked = false;
    private List<String> friendList = new ArrayList<>();
    private List<String> showList = new ArrayList<>();

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

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_invite:{
                clickInvite();
                break;
            }
            case R.id.btn_access:{

                break;
            }
            case R.id.btn_cancel:{
                onBackPressed();
                break;
            }
        }
    };

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

}
