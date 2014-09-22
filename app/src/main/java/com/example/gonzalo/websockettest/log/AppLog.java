package com.example.gonzalo.websockettest.log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Gonzalo on 21-09-2014.
 */
public class AppLog {
    List<LogLine> logLines;
    Set<AppLogObserver> observers;

    public AppLog() {
        logLines = new ArrayList<LogLine>();
        observers = new HashSet<AppLogObserver>();
    }

    public List<LogLine> getLast(int lines) {
        return logLines.subList(0,
                lines > logLines.size() ? logLines.size() : lines);
    }

    public void log(String str) {
        logLines.add(new LogLine(str));
        notifyObservers();
    }

    public void notifyObservers() {
        for(AppLogObserver o : observers) {
            o.onLogUpdated(this);
        }
    }

    public void addObserver(AppLogObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(AppLogObserver observer) {
        observers.remove(observer);
    }

    public class LogLine {
        private Date date;
        private String content;

        public LogLine(String content) {
            date = new Date();
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public Date getDate() {
            return date;
        }
    }

    public interface AppLogObserver {
        public void onLogUpdated(AppLog log);
    }
}
