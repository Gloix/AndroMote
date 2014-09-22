package com.example.gonzalo.websockettest.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.example.gonzalo.websockettest.log.AppLog;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
* Created by Gonzalo on 21-09-2014.
*/
public class MyWebSocketServer extends WebSocketServer {
    private static final String TAG = "SensingApp::MyWebSocketServer";
    public static final int STATE_DISCONNECTED = 0, STATE_CONNECTED = 1;
    private AppLog appLog;
    private Integer state = STATE_DISCONNECTED;
    private List<ServerStatusObserver> observers;
    private Context context;
    public MyWebSocketServer(Context context, int port) {
        super(new InetSocketAddress(port));
        this.context = context;
        observers = new ArrayList<ServerStatusObserver>();
    }

    @Override
    public void start() {
        synchronized (state) {
            super.start();
            state = STATE_CONNECTED;
            appLog.log("Server listening. IP:" + getLocalIpAddress()+":"+getPort());
            notifyStateChanged();
        }
    }

    @Override
    public void stop() throws IOException, InterruptedException {
        synchronized (state) {
            super.stop();
            appLog.log("Server stopped");
            state = STATE_DISCONNECTED;
            notifyStateChanged();
        }
    }

    public int getState() {
        synchronized (state) {
            return state;
        }
    }

    @Override
    public void stop(int timeout) throws IOException, InterruptedException {
        synchronized (state) {
            super.stop(timeout);
            state = STATE_DISCONNECTED;
            notifyStateChanged();
        }
    }

    public void notifyStateChanged() {
        for(ServerStatusObserver obs : observers) {
            obs.onServerStatusChanged(state);
        }
    }

    public void setAppLog(AppLog log) {
        appLog = log;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        if(appLog != null)
            appLog.log("Connected peer(" + conn.getRemoteSocketAddress() + ")");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if(appLog != null)
            appLog.log("Disconnected peer(" + conn.getRemoteSocketAddress() + ")."
                        + "Reason: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if(appLog != null)
            appLog.log("Msg("+conn.getRemoteSocketAddress()+"):"+message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(appLog != null)
            appLog.log("Error:"+ex.getMessage());
    }

    public void sendToAll(Object obj) {
        String str = obj.toString();
        for(WebSocket w : connections()) {
            try {
                w.send(str);
            } catch(WebsocketNotConnectedException e) {
                if(appLog != null)
                    appLog.log("Tried to send string to "+w.getRemoteSocketAddress()
                                + " but failed.");
            }
        }
    }

    public interface ServerStatusObserver {
        public void onServerStatusChanged(int status);
    }

    public void addStateObserver(ServerStatusObserver observer) {
        observers.add(observer);
    }

    public void removeStateObserver(ServerStatusObserver observer) {
        observers.remove(observer);
    }

    public String getLocalIpAddress() {
//        try {
//            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//                NetworkInterface intf = en.nextElement();
//                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//                    InetAddress inetAddress = enumIpAddr.nextElement();
//                    if (!inetAddress.isLoopbackAddress()) {
//                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//                        return ip;
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//            Log.e(TAG, ex.toString());
//        }
//        return null;
//        try {
//            return InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return "Unknown";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }
}
