package com.example.eaglestreamer;

import com.example.eaglestreamer.Orientation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

public class MainActivity extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private TextView mText;
    private ImageView mScreen;

    private Publisher<Orientation> oriPub_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        oriPub_ = new Publisher<>(9999);

        mScreen = findViewById(R.id.screen);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Orientation ori = new Orientation();
        ori.azimut_= event.values[0];
        ori.pitch_= event.values[1];
        ori.roll_= event.values[2];
        //Log.d("EAGLE_STREAMER", String.valueOf(ori.azimut_)+", "+String.valueOf(ori.pitch_)+", "+String.valueOf(ori.roll_));
        oriPub_.publish(ori);
    }
}