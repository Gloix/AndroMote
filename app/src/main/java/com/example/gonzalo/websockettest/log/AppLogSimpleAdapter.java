package com.example.gonzalo.websockettest.log;

import android.os.Handler;
import android.widget.TextView;

import com.example.gonzalo.websockettest.log.AppLog;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Gonzalo on 21-09-2014.
 */
public class AppLogSimpleAdapter implements AppLog.AppLogObserver {
    AppLog appLog;
    int maxLines;
    Handler handler;
    TextView textView;
    SimpleDateFormat dateFormat;
    public AppLogSimpleAdapter(Handler handler, TextView textView, AppLog appLog, int maxLines) {
        this.handler = handler;
        this.textView = textView;
        this.appLog = appLog;
        this.maxLines = maxLines;
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        appLog.addObserver(this);
    }

    @Override
    public void onLogUpdated(AppLog log) {
        List<AppLog.LogLine> lines =  appLog.getLast(maxLines);
        final StringBuilder sb = new StringBuilder();

        for(AppLog.LogLine line : lines) {
            sb.append(dateFormat.format(line.getDate()));
            sb.append(":");
            sb.append(line.getContent());
            sb.append("\n");
        }
        handler.post(new Runnable() {
            public void run() {
                textView.setText(sb.toString());
            }
        });
    }
}
