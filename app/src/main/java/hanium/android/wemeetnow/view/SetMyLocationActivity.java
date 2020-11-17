package hanium.android.wemeetnow.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.json.JSONException;
import org.json.JSONObject;

import hanium.android.wemeetnow.MyApplication;
import hanium.android.wemeetnow.R;
import io.socket.emitter.Emitter;

public class SetMyLocationActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private TMapGpsManager gps;
    private TMapView tmapview;
    private TMapPoint centerPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        setMapView();
        setButton();
        startGps();

    }

    @Override
    public void onLocationChange(Location location) {
        TMapPoint point = gps.getLocation();
        runOnUiThread(() -> tmapview.setCenterPoint(point.getLongitude(), point.getLatitude()));
    }

    private void startGps() {
        gps = new TMapGpsManager(this);
        gps.setProvider(TMapGpsManager.NETWORK_PROVIDER);
        gps.OpenGps();
    }

    private void setMapView() {
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey(getString(R.string.tmap_app_key));

        RelativeLayout rl_tmap = findViewById(R.id.rl_tmap);
        rl_tmap.addView(tmapview);
    }

    private void setButton() {
        Button button = findViewById(R.id.btn_ok);
        button.setOnClickListener(view -> {
            JSONObject obj = new JSONObject();
            try {
                Intent intent = getIntent();
                obj.put("total_partyCount", intent.getIntExtra("totalCount", 0));

                centerPoint = tmapview.getCenterPoint();
                obj.put("member_placelat", centerPoint.getLatitude());
                obj.put("member_placelong", centerPoint.getLongitude());

                obj.put("head", intent.getStringExtra("head"));
                obj.put("party_name", intent.getStringExtra("partyName"));

                MyApplication.socket.emit("join_party", obj);
                MyApplication.socket.on("SUCCESS_INSERT_MEMBER", onSuccess);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    Emitter.Listener onSuccess = args -> {
        Log.d("socket", "MyLocation Success");
        runOnUiThread(() -> {
            Intent intent = new Intent();
            intent.putExtra("startLongitude", centerPoint.getLongitude());
            intent.putExtra("startLatitude", centerPoint.getLatitude());
            setResult(200, intent);
            finish();
        });
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.CloseGps();
    }
}