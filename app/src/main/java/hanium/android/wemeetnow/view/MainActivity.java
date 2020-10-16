package hanium.android.wemeetnow.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hanium.android.wemeetnow.MyApplication;
import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.adapter.FriendListAdapter;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.util.PreferenceManager;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    private Location currentLocation;
    private List<String> friendList = new ArrayList<>();
    private FriendListAdapter adapter;
    private int totalCount = 0, memberCount = 0;

    private DrawerLayout drawerLayout;
    private EditText et_search;
    private TMapView tmapview;
    private TextView tv_partyname, tv_partyplace, tv_partytime, tv_partymember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        initialize();
        setMapView();
        setRecyclerView();

        MyApplication.socket.on("chosen", onFriendInvitationReceived);
        MyApplication.socket.on("friend_list", onFriendListReceived);
        MyApplication.socket.on("invite_party", onPartyInvitationReceived);
        MyApplication.socket.on("member_count", onMemberCountReceived);

    }



    private void getPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_friends);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FriendListAdapter(friendList);
        recyclerView.setAdapter(adapter);
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            getMyLocation();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {

        }
    };

    private void getMyLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;
                        tmapview.setCenterPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
                        tmapview.setIconVisibility(true);
                        tmapview.setLocationPoint(currentLocation.getLongitude(), currentLocation.getLatitude());
                    }
                });
    }

    androidx.drawerlayout.widget.DrawerLayout.DrawerListener drawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            Log.d("socket", "Refresh Friend");
            MyApplication.socket.emit("refresh_friend");
        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    private void initialize() {
        // 메인
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(drawerListener);

        AppCompatImageButton btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setOnClickListener(onClickListener);

        et_search = findViewById(R.id.et_search);
        et_search.setOnEditorActionListener(onEditorActionListener);

        AppCompatImageButton btn_location = findViewById(R.id.btn_location);
        btn_location.setOnClickListener(onClickListener);

        AppCompatImageButton btn_addparty = findViewById(R.id.btn_addparty);
        btn_addparty.setOnClickListener(onClickListener);

        // 드로어
        TextView drawer_name = findViewById(R.id.drawer_name);
        drawer_name.setText(PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.NAME, null));

        tv_partyname = findViewById(R.id.tv_partyname);

        tv_partyplace = findViewById(R.id.tv_partyplace);

        tv_partytime = findViewById(R.id.tv_partytime);

        Button btn_choice = findViewById(R.id.btn_choice);

        tv_partymember = findViewById(R.id.tv_partymember);

        AppCompatImageButton btn_addfriend = findViewById(R.id.btn_addfriend);
        btn_addfriend.setOnClickListener(onClickListener);

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(onClickListener);
    }

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchPlace();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(et_search.getWindowToken(), 0);
                }
                handled = true;
            }
            return handled;
        }
    };

    private void searchPlace() {
        tmapview.removeAllMarkerItem();

        TMapData data = new TMapData();
        data.findAllPOI(et_search.getText().toString(), poiItem -> {
            ArrayList<TMapPoint> arrays = new ArrayList<>();

            for(int i = 0; i < poiItem.size(); i++) {
                TMapPOIItem item = poiItem.get(i);
                Log.d("MainActivity", "POI Name: " + item.getPOIName().toString() + ", " +
                        "Address: " + item.getPOIAddress().replace("null", "")  + ", " +
                        "Point: " + item.getPOIPoint().toString());
                TMapMarkerItem markerItem = new TMapMarkerItem();
                TMapPoint tMapPoint = new TMapPoint(item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                arrays.add(tMapPoint);

                markerItem.setPosition(0.5f, 1.0f);
                markerItem.setTMapPoint(tMapPoint);
                markerItem.setName(item.getPOIName());
                markerItem.setCalloutTitle(item.getPOIName());
                markerItem.setCanShowCallout(true);

                tmapview.addMarkerItem(item.getPOIID(), markerItem);
            }

            TMapInfo info = tmapview.getDisplayTMapInfo(arrays);
            tmapview.setZoomLevel(info.getTMapZoomLevel());
            tmapview.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude());
        });
    }

    private void setMapView() {
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));

        RelativeLayout rl_tmap = findViewById(R.id.rl_tmap);
        rl_tmap.addView(tmapview);
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()){
            case R.id.btn_menu:{
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.btn_location:{
                tmapview.setCenterPoint(currentLocation.getLongitude(), currentLocation.getLatitude(), true);
                break;
            }
            case R.id.btn_logout:{
                MyApplication.disconnectSocket();
                PreferenceManager.getInstance().resetSharedPreference(getApplicationContext());
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btn_addfriend:{
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.btn_addparty:{
                Intent intent = new Intent(MainActivity.this, AddPartyActivity.class);
                startActivityForResult(intent, 100);
                break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 100) {
            String partyName = data.getStringExtra("partyName");
            totalCount = data.getIntExtra("totalCount", 0);

            tv_partyname.setText(partyName);
            tv_partymember.setText(memberCount + "/" + totalCount);
            tv_partytime.setText(data.getStringExtra("time"));

            goToSetMyLocation(totalCount,
                    PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.ID, null),
                    partyName);
        }
    }

    Emitter.Listener onFriendInvitationReceived = args -> {

        JSONObject obj = (JSONObject)args[0];
        try {
            String sender = obj.getString("sender");
            String senderName = obj.getString("senderName");
            Log.d("socket", "Friend Invitation: " +senderName);
            runOnUiThread(() -> showFriendAlertDialog(sender, senderName));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    };

    Emitter.Listener onPartyInvitationReceived = args -> {
        JSONObject obj = (JSONObject)args[0];
        try {
            String party_name = obj.getString("party_name");
            String head = obj.getString("head");
            String head_name = obj.getString("head_name");
            totalCount = obj.getInt("total_partyCount");
            Log.d("socket", "Party Invitation: " + head_name + ", " + party_name);
            runOnUiThread(() -> {
                showPartyAlertDialog(party_name, head, head_name, totalCount);
                tv_partyname.setText(party_name);
                tv_partymember.setText(memberCount + "/" + totalCount);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    private void showFriendAlertDialog(String sender, String senderName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("친구 신청").setMessage(senderName + "님에게 친구 신청이 도착했습니다.");

        builder.setPositiveButton("수락", (dialog, id) -> {
            JSONObject obj = new JSONObject();
            try {
                obj.put("sender", sender);
                obj.put("senderName", senderName);
                MyApplication.socket.emit("yes_friend", obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton("거절", (dialog, id) -> {
        });

        if(!((Activity) MainActivity.this).isFinishing())
        {
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void showPartyAlertDialog(String party_name, String head, String head_name, int totalCount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("파티 초대").setMessage(head_name + "님에게 " + party_name + " 파티 초대가 도착했습니다.");

        builder.setPositiveButton("확인", (dialog, id) -> goToSetMyLocation(totalCount, head, party_name));

        if(!((Activity) MainActivity.this).isFinishing())
        {
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void goToSetMyLocation(int totalCount, String head, String partyName) {
        Intent intent = new Intent(MainActivity.this, SetMyLocationActivity.class);
        intent.putExtra("totalCount", totalCount);
        intent.putExtra("head", head);
        intent.putExtra("partyName", partyName);
        startActivity(intent);
    }

    Emitter.Listener onFriendListReceived = args -> {

        Log.d("socket", "Friend List: " + args[0] + "");
        friendList.clear();
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

    Emitter.Listener onMemberCountReceived = args -> {
        JSONObject obj = (JSONObject) args[0];

        Log.d("socket", "Member Count: " + obj);

        String time = "";
        try {
            memberCount = obj.getInt("member_count");
            time = obj.getString("time_info");
            Log.d("socket", "count: " + memberCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalTime = time;
        runOnUiThread(() -> {
            tv_partymember.setText(memberCount + "/" + totalCount);
            tv_partytime.setText(finalTime);
        });
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}