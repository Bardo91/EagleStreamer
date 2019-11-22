package com.example.eaglestreamer;

import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Byteable;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Callable;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Publisher;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Subscriber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class PacketInt implements Byteable {

    public int data;

    @Override
    public byte[] getBytes() {
        byte[] bytes = ByteBuffer.allocate(4).putInt(data).array();
        return bytes;
    }

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public void parse(byte[] _data) {
        ByteBuffer buf = ByteBuffer.wrap(_data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        data = buf.getInt();
    }
}

public class MainActivity extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private EditText ipInput_;
    private ImageView mScreen;

    private Publisher<Orientation> oriPub_;

    private Subscriber<PacketInt> counterSub_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ipInput_ = new EditText(this);
        ipInput_.setInputType(InputType.TYPE_CLASS_PHONE);

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

        // IP SELECTOR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose PC IP")
                        .setView(ipInput_)
                        .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                counterSub_ = new Subscriber<>(ipInput_.getText().toString(), 9998, new PacketInt());
                                counterSub_.registerCallback(new Callable<PacketInt>(){
                                    @Override
                                    public void run(PacketInt _data){
                                        Log.d("EAGLE_STREAMER", String.valueOf(_data.data));
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Do nothing", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        Log.d("EAGLE_STREAMER", String.valueOf(ori.azimut_)+", "+String.valueOf(ori.pitch_)+", "+String.valueOf(ori.roll_));
        oriPub_.publish(ori);
    }
}