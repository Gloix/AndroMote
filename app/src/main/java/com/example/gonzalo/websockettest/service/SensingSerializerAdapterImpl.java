package com.example.gonzalo.websockettest.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.example.gonzalo.websockettest.service.SensingSerializerAdapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Gonzalo on 21-09-2014.
 */
public class SensingSerializerAdapterImpl extends SensingSerializerAdapter {
    NumberFormat numberFormat;
    public SensingSerializerAdapterImpl(Context context) {
        super(context);
        numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMaximumFractionDigits(3);
    }

    @Override
    public String serialize(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(numberFormat.format(values[0]));
            for(int i=1 ; i < values.length; i++) {
                stringBuilder.append("\t");
                stringBuilder.append(numberFormat.format(values[i]));
            }
            return stringBuilder.toString();
        }
        return "";
    }
}
