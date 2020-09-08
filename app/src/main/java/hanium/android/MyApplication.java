package hanium.android;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MyApplication extends Application {

    public static Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();

        connectSocket();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        disconnectSocket();
    }

    private void connectSocket() {
        try {
            socket = IO.socket("http://ec2-3-15-44-226.us-east-2.compute.amazonaws.com:8080");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void disconnectSocket() {
        socket.disconnect();
    }
}
