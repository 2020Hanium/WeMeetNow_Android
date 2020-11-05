package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapInfo;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hanium.android.wemeetnow.MyApplication;
import hanium.android.wemeetnow.R;
import io.socket.emitter.Emitter;

public class SelectPathActivity extends AppCompatActivity {

    private TMapView tmapview;
    private String path = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_path);

        initialize();
        setMapView();
    }

    private void initialize() {
        RadioGroup rg_path = findViewById(R.id.rg_path);
        rg_path.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.rb_car: {
                    path = "자동차";
                    break;
                }
                case R.id.rb_bus: {
                    path = "버스";
                    break;
                }
                case R.id.rb_subway: {
                    path = "지하철";
                    break;
                }
                case R.id.rb_walk: {
                    path = "도보";
                    break;
                }
                case R.id.rb_etc: {
                    path = "기타";
                    break;
                }
            }
        });

        Button btn_ac = findViewById(R.id.btn_ac);
        btn_ac.setOnClickListener(onClickListener);
        Button btn_cc = findViewById(R.id.btn_cc);
        btn_cc.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = view -> {
        switch (view.getId()) {
            case R.id.btn_ac: {
                sendPath();
                break;
            }
            case R.id.btn_cc: {
                onBackPressed();
                break;
            }
        }
    };

    private void setMapView() {
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));

        RelativeLayout rl_tmap = findViewById(R.id.rl_tmap);
        rl_tmap.addView(tmapview);

        Intent intent = getIntent();
        double startLongitude = intent.getDoubleExtra("startLongitude", 126.988205);
        double startLatitude = intent.getDoubleExtra("startLatitude", 37.551135);
        double placeLongitude = intent.getDoubleExtra("placeLongitude", 126.988205);
        double placeLatitude = intent.getDoubleExtra("placeLatitude", 37.551135);

        TMapPoint point1 = new TMapPoint(startLatitude, startLongitude);
        TMapPoint point2 = new TMapPoint(placeLatitude, placeLongitude);

        ArrayList<TMapPoint> arrays = new ArrayList<>();
        arrays.add(point1);
        arrays.add(point2);

        TMapData tmapdata = new TMapData();
        tmapdata.findPathData(point1, point2, polyLine -> runOnUiThread(() -> {
            tmapview.addTMapPath(polyLine);

            TMapInfo info = tmapview.getDisplayTMapInfo(arrays);
            tmapview.setZoomLevel(info.getTMapZoomLevel());
            tmapview.setCenterPoint(info.getTMapPoint().getLongitude(), info.getTMapPoint().getLatitude());
        }));
    }

    private void sendPath() {
        Log.d("socket", "selected path: " + path);

        JSONObject obj = new JSONObject();
        try {
            obj.put("path", path);

            MyApplication.socket.emit("select_path", obj);
            MyApplication.socket.on("success_select_path", onSuccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Emitter.Listener onSuccess = args -> {
        Log.d("socket", "Path Success");
        finish();
    };
}
