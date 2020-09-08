package hanium.android.wemeetnow.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
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

import java.util.ArrayList;
import java.util.List;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.util.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private Location currentLocation;

    private DrawerLayout drawerLayout;
    private EditText et_search;
    private TMapView tmapview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();
        initialize();
        setMapView();

    }

    private void getPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("[설정] > [권한] 에서 권한을 허용해주세요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
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

    private void initialize() {
        // 메인
        drawerLayout = findViewById(R.id.drawer_layout);

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

        TextView tv_partyname = findViewById(R.id.tv_partyname);

        TextView tv_partyplace = findViewById(R.id.tv_partyplace);

        TextView tv_partytime = findViewById(R.id.tv_partytime);

        Button btn_choice = findViewById(R.id.btn_choice);

        TextView tv_partymember = findViewById(R.id.tv_partymember);

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
        }
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
