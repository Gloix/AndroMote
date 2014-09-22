package com.example.gonzalo.websockettest.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.gonzalo.websockettest.log.AppLog;

import org.java_websocket.server.WebSocketServer;

import java.io.IOException;

public class ServerService extends Service implements SensingSerializerAdapter.SensingObserver {
    private static final int onGoingNotificationId = 0;
    private MyWebSocketServerFactory serverFactory;
    private MyWebSocketServer server;
    private AppLog appLog;
    private SensingSerializerAdapter serializer;

    public ServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appLog = new AppLog();
        serializer = new SensingSerializerAdapterImpl(this);
        serializer.registerSensingObserver(this);
        serializer.startSensing();
        serverFactory = new MyWebSocketServerFactory();
        serverFactory.setAppLog(appLog);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ServerServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serializer.unregisterSensingObserver(this);
        try {
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(onGoingNotificationId);
    }

    private void startServer() {
        if(server != null) {
            stopServer();
        }
        server = serverFactory.getNew(this, 9998);
        try {
            server.start();
        } catch(IllegalStateException e) {
            appLog.log("Server could not be started. Exception:" + e.getMessage());
        }
    }

    private void stopServer() {
        if(server != null)
            try {
                server.stop();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public AppLog getLog() {
        return appLog;
    }

    @Override
    public void onReading(int sensorType, String serialized) {
        if(server != null)
            server.sendToAll(serialized);
    }

    public class ServerServiceBinder extends Binder {
        public AppLog getAppLog() {
            return appLog;
        }

        public void addServerStateObserver(MyWebSocketServer.ServerStatusObserver observer) {
            serverFactory.addStatusObserver(observer);
        }

        public void startServer() {
            ServerService.this.startServer();
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(ServerService.this)
                    .setContentText("Tap to open app")
                    .setContentTitle("Sensing ongoing")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .build();
            notificationManager.notify(onGoingNotificationId, notification);
        }

        public void stopServer() {
            ServerService.this.stopServer();
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(onGoingNotificationId);
        }

        public Integer getServerState() {
            if(ServerService.this.server!= null)
                return ServerService.this.server.getState();
            return MyWebSocketServer.STATE_DISCONNECTED;
        }
    }


}
