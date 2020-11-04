package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hanium.android.wemeetnow.MyApplication;
import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.etc.Constant;
import hanium.android.wemeetnow.util.PreferenceManager;
import io.socket.emitter.Emitter;

public class SelectPlaceActivity extends AppCompatActivity {

    private double longitude, latitude;

    private TMapView tmapview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        initialize();
        setMapView();
    }

    private void initialize() {
        Button btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(view -> getSelectedLocation());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("약속 장소 선택");
    }

    private void setMapView() {
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));

        RelativeLayout rl_tmap = findViewById(R.id.rl_tmap);
        rl_tmap.addView(tmapview);

        Intent intent = getIntent();
        longitude = intent.getDoubleExtra("longitude", 126.988205);
        latitude = intent.getDoubleExtra("latitude", 37.551135);
        tmapview.setCenterPoint(longitude, latitude);

        getMiddlePlaces();
    }

    private void getMiddlePlaces() {
        TMapData tmapdata = new TMapData();
        TMapPoint point = new TMapPoint(latitude, longitude);
        tmapdata.findAroundNamePOI(point, "카페;음식점;노래방;지하철;마트", 2, 30, poiItem -> {
            ArrayList<TMapPoint> arrays = new ArrayList<>();

            for (int i = 0; i < poiItem.size(); i++) {
                TMapPOIItem item = poiItem.get(i);
                Log.d("POI Name: ", item.getPOIName().toString() + ", " +
                        "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                        "Point: " + item.getPOIPoint().toString() + ", " +
                        "Id: " + item.getPOIID());
                TMapMarkerItem markerItem = new TMapMarkerItem();
                TMapPoint tMapPoint = item.getPOIPoint();
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

    private void getSelectedLocation() {
        JSONObject obj = new JSONObject();
        try {
            TMapPoint point = tmapview.getCenterPoint();
            obj.put("place_latitude", point.getLatitude());
            obj.put("place_longitude", point.getLongitude());

            obj.put("head", PreferenceManager.getInstance().getSharedPreference(getApplicationContext(), Constant.Preference.ID, null));

            MyApplication.socket.emit("select_place", obj);
            MyApplication.socket.on("success_select_place", onSuccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Emitter.Listener onSuccess = args -> {
        Log.d("socket", "Place Success");
        finish();
    };
}
