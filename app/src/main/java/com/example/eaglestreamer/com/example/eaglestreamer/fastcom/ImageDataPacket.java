package com.example.eaglestreamer.com.example.eaglestreamer.fastcom;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageDataPacket implements Byteable {
    public int PACKET_SIZE = 1024;
    public Boolean isFirst = false;
    public int packetId = 0;
    public int numPackets = 0;
    public int totalSize = 0;
    public int packetSize = 0;
    public byte[] buffer = new byte[PACKET_SIZE];

    @Override
    public byte[] getBytes() {
        ByteArrayOutputStream packet = new BufferedOutputStream();
        packet.write(PACKET_SIZE);
        packet.write((byte)(isFirst? 1:0));
        packet.write(packetId);
        packet.write(numPackets);
        packet.write(totalSize);
        packet.write(packetSize);
        try {
            packet.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
        return buffer;
    }

    @Override
    public int getSize() {
        return 4+1+4+4+4+4+1024;
    }

    @Override
    public void parse(byte[] _data) {

    }
}