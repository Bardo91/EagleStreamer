package com.example.eaglestreamer;

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
        return new byte[0];
    }

    @Override
    public int getSize() {
        return 4+1+4+4+4+4+1024;
    }

    @Override
    public void parse(byte[] _data) {

    }
}