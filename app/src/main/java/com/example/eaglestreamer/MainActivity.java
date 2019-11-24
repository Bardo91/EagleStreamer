package com.example.eaglestreamer;

import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Byteable;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Callable;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.ImageSubscriber;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Publisher;
import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Subscriber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mOrientation;
    private EditText ipInput_;
    private ImageView mScreen;

    private Publisher<Orientation> oriPub_;

    private ImageSubscriber imageSubscriber_;

    private long lastTime = System.nanoTime();

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

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OPENCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()) {
            Log.d("OPENCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d("OPENCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_UI);

        // IP SELECTOR
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose PC IP")
                        .setView(ipInput_)
                        .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                imageSubscriber_ = new ImageSubscriber(ipInput_.getText().toString(), 9777);
                                imageSubscriber_.registerCallback(new Callable<Mat>(){

                                    @Override
                                    public void run(Mat _data){
                                        //Log.d("EAGLE_STREAMER", "received new image");
                                        final Mat image = _data.clone();
                                        double incT = (System.nanoTime()-lastTime)*10e-9;
                                        //Log.d("EAGLE_STREAM", "IncT: " + String.valueOf(incT)+". FPS: " + String.valueOf(1/incT));
                                        lastTime = System.nanoTime();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Bitmap bmp = Bitmap.createBitmap(image.width(), image.height(), Bitmap.Config.RGB_565);
                                                Utils.matToBitmap(image, bmp);
                                                mScreen.setImageBitmap(bmp);
                                            }
                                        });
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
        // Log.d("EAGLE_STREAMER", String.valueOf(ori.azimut_)+", "+String.valueOf(ori.pitch_)+", "+String.valueOf(ori.roll_));
        oriPub_.publish(ori);
    }
}