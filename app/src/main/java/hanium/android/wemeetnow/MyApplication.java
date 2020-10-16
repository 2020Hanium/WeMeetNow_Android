package hanium.android.wemeetnow;

import android.app.Application;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyApplication extends Application {

    public static final String BASE_URL = "http://ec2-3-15-44-226.us-east-2.compute.amazonaws.com:8080";

    public static Socket socket;

    @Override
    public void onTerminate() {
        super.onTerminate();

        disconnectSocket();
    }

    public static void connectSocket() {
        try {
            socket = IO.socket(BASE_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();

        socket.on(Socket.EVENT_CONNECT, onConnect);
    }

    public static void disconnectSocket() {
        socket.disconnect();
        Log.d("socket", "disconnect");
    }

    static Emitter.Listener onConnect = args -> Log.d("socket", "connect");

}
