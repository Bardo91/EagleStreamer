package com.example.eaglestreamer.com.example.eaglestreamer.fastcom;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Vector;


public class Publisher<T_ extends  Byteable> {

    private DatagramSocket serverSocket_;
    private Thread listenThread_;
    private int port_;
    private Boolean run_;
    private Vector<InetAddress> addrList_ = new Vector<>();
    private Vector<Integer> portList_ = new Vector<>();

    public Publisher(int _port){
        port_ = _port;
        run_ = true;

        listenThread_ = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket_ = new DatagramSocket(port_);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                while(run_){
                    byte[] receiveData = new byte[1];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    Log.d("EAGLE_STREAMER", "Received new connection");
                    try {
                        serverSocket_.receive(receivePacket);
                    } catch (IOException e) {
                        run_ = false;
                        e.printStackTrace();
                        continue;
                    }

                    addrList_.add(receivePacket.getAddress());
                    portList_.add(receivePacket.getPort());


                }
            }
        });
        listenThread_.start();
    }


    public void publish(T_ _data){
        byte[] bytes = _data.getBytes();

        if(addrList_.size() > 0 && addrList_.size() == portList_.size()) {
            for (int i = 0; i < addrList_.size(); i++) {
                DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, addrList_.get(i), portList_.get(i));
                //Log.d("EAGLE_STREAMER", "Sending packet of size: " + String.valueOf(bytes.length));
                try {
                    serverSocket_.send(sendPacket);
                    //Log.d("EAGLE_STREAMER", "sent packet");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
