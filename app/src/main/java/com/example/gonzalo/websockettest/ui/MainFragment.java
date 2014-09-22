package com.example.gonzalo.websockettest.ui;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gonzalo.websockettest.log.AppLogSimpleAdapter;
import com.example.gonzalo.websockettest.service.MyWebSocketServer;
import com.example.gonzalo.websockettest.R;
import com.example.gonzalo.websockettest.service.ServerService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements MyWebSocketServer.ServerStatusObserver, View.OnClickListener {
    private static final String TAG = "SensingApp::MainFragment";
    ServerService.ServerServiceBinder binder;
    TextView logTextView;
    Button startStopButton;
    ServiceConnection serviceConnection;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        logTextView = (TextView) rootView.findViewById(R.id.LogDisplay);
        startStopButton = (Button) rootView.findViewById(R.id.StartStopServer);

        startStopButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    binder = (ServerService.ServerServiceBinder) iBinder;
                    AppLogSimpleAdapter logAdapter = new AppLogSimpleAdapter(new Handler(),
                            logTextView, binder.getAppLog(), 20);

                    binder.addServerStateObserver(MainFragment.this);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    binder = null;
                    Log.w(TAG, "Service disconnected");
                }
            };
        }
        getActivity().bindService(new Intent(getActivity(),
                ServerService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServerStatusChanged(final int status) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (status == MyWebSocketServer.STATE_CONNECTED) {
                    startStopButton.setText("Stop");
                } else {
                    startStopButton.setText("Start");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == startStopButton){
            if (binder != null) {
                if (binder.getServerState() == MyWebSocketServer.STATE_CONNECTED) {
                    binder.stopServer();
                } else if (binder.getServerState() == MyWebSocketServer.STATE_DISCONNECTED) {
                    binder.startServer();
                }
            }
        }
    }
}
