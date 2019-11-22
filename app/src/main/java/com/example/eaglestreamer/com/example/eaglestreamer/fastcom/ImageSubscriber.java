package com.example.eaglestreamer.com.example.eaglestreamer.fastcom;

import android.renderscript.Matrix3f;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import org.opencv.core.Mat;

public class ImageSubscriber {

    private Subscriber<ImageDataPacket> packetSubscriber_;

    private boolean isFirst_ = true;
    private int packetId_ = -1;
    private ByteArrayOutputStream totalBuffer_ = new ByteArrayOutputStream();

    public ImageSubscriber(String _ip, int _port){
        packetSubscriber_ = new Subscriber<>(_ip, _port, new ImageDataPacket());

        packetSubscriber_.registerCallback(new Callable<ImageDataPacket>() {
            @Override
            public void run(ImageDataPacket _data) {
                if(isFirst_ == _data.isFirst){
                    isFirst_ = false;
                    if(packetId_+1 == _data.packetId){
                        if(totalBuffer_.size() == _data.totalSize){
                            // Decode buffer
                            Mat image = imdecode(totalBuffer_, 1);

                            // Call callbacks

                            // Reset data
                            isFirst_ = true;
                            packetId_ = -1;
                            totalBuffer_.reset();
                        }
                    }
                }
            }
        });
    }

}


