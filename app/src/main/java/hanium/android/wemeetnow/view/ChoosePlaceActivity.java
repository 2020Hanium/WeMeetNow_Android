package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.List;

import hanium.android.wemeetnow.R;
import hanium.android.wemeetnow.adapter.PlacesAdapter;
import hanium.android.wemeetnow.model.Place;

public class ChoosePlaceActivity extends AppCompatActivity {

    private double longitude, latitude;
    private List<Place> list = new ArrayList<>();

    private TMapView tmapview;

    private PlacesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_place);

        initialize();
        setRecyclerView();
        setMapView();
    }

    private void initialize() {
        Button btn_ac = findViewById(R.id.btn_ac);
        btn_ac.setOnClickListener(onClickListener);
        Button btn_cc = findViewById(R.id.btn_cc);
        btn_cc.setOnClickListener(onClickListener);
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

    private void setRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_places);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PlacesAdapter(list);
        recyclerView.setAdapter(adapter);
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
                        "Point: " + item.getPOIPoint().toString());
                TMapMarkerItem markerItem = new TMapMarkerItem();
                TMapPoint tMapPoint = new TMapPoint(item.getPOIPoint().getLatitude(), item.getPOIPoint().getLongitude());

                list.add(new Place(item.getPOIName(), item.getPOIAddress().replace("null", "")));

                arrays.add(tMapPoint);

                markerItem.setPosition(0.5f, 1.0f);
                markerItem.setTMapPoint(tMapPoint);
                markerItem.setName(item.getPOIName());
                markerItem.setCalloutTitle(item.getPOIName());
                markerItem.setCanShowCallout(true);

                tmapview.addMarkerItem(item.getPOIID(), markerItem);
            }

            runOnUiThread(() -> adapter.notifyDataSetChanged());

            TMapInfo info = tmapview.getDisplayTMapInfo(arrays);
            tmapview.setZoomLevel(info.getTMapZoomLevel());
            tmapview.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude());
        });
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.btn_ac: {
                break;
            }
            case R.id.btn_cc: {
                onBackPressed();
                break;
            }
        }
    };
}
