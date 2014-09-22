package com.example.gonzalo.websockettest.service;

import android.content.Context;

import com.example.gonzalo.websockettest.log.AppLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by Gonzalo on 21-09-2014.
 */
public class MyWebSocketServerFactory {
    private AppLog appLog;
    private List<MyWebSocketServer.ServerStatusObserver> observers;

    public MyWebSocketServerFactory() {
        observers = new ArrayList<MyWebSocketServer.ServerStatusObserver>();
    }

    public void setAppLog(AppLog appLog) {
        this.appLog = appLog;

    }

    public MyWebSocketServer getNew(Context context, int port) {
        MyWebSocketServer server = new MyWebSocketServer(context, port);
        server.setAppLog(appLog);
        for(MyWebSocketServer.ServerStatusObserver observer : observers) {
            server.addStateObserver(observer);
        }
        return server;
    }


    public void addStatusObserver(MyWebSocketServer.ServerStatusObserver observer) {
        observers.add(observer);
    }

    public void removeStatusObserver(MyWebSocketServer.ServerStatusObserver observer) {
        observers.remove(observer);
    }
}
