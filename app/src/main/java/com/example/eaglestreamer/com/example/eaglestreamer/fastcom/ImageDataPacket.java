package com.example.eaglestreamer.com.example.eaglestreamer.fastcom;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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
        ByteArrayOutputStream packet = new ByteArrayOutputStream();
        packet.write(PACKET_SIZE);
        packet.write(isFirst? 1:0);
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
        return      4   // Int with size of packet
                +   4   // This should be a boolean, but packet is more efficient with ints, so faking it with int as it comes from c
                +   4   // Int Packet id
                +   4   // int num packets
                +   4   // int total size
                +   4   // int packet size
                +   1024;   // real buffer
    }

    @Override
    public void parse(byte[] _data) {
        ByteBuffer buf = ByteBuffer.wrap(_data);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        PACKET_SIZE = 12;
        PACKET_SIZE = buf.getInt();
        isFirst =  !(buf.getInt() == 0);
        packetId = buf.getInt();       /*hardcoding*/ isFirst = packetId == 0;
        numPackets = buf.getInt();
        totalSize = buf.getInt();
        packetSize = buf.getInt();
        buffer = Arrays.copyOfRange(buf.array(), buf.position(), buf.limit());
    }
}