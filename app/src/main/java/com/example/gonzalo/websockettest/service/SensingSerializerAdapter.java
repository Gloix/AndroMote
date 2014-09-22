package com.example.gonzalo.websockettest.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages active sensors and serializes the sensor readings to strings given by
 * inheriting classes.
 * Created by Gonzalo on 21-09-2014.
 */
public abstract class SensingSerializerAdapter implements SensorEventListener {
    private static final String TAG = "SensingApp::SensingSerializerAdapter";
    List<SensingObserver> observers;
    StringBuilder stringBuilder;
    SensorManager sensorManager;
    IBinder serverBinder;
    Context context;

    public SensingSerializerAdapter(Context context) {
        this.context = context;
        this.observers = new ArrayList<SensingObserver>();
    }

    public void startSensing() {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void registerSensingObserver(SensingObserver observer) {
        observers.add(observer);
    }

    public void unregisterSensingObserver(SensingObserver observer) {
        observers.remove(observer);
    }

    public void stopSensing() {
        sensorManager.unregisterListener(this);
    }

    private void notifyObservers(int sensorType, String serialized) {
        for(SensingObserver obs : observers) {
            obs.onReading(sensorType, serialized);
        }
    }

    public abstract String serialize(SensorEvent event);

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        notifyObservers(sensorEvent.sensor.getType(), serialize(sensorEvent));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public interface SensingObserver {
        public void onReading(int sensorType, String serialized);
    }
}
