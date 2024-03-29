package com.example.eaglestreamer;

import com.example.eaglestreamer.com.example.eaglestreamer.fastcom.Byteable;

public class Orientation implements Byteable {
    public float x;
    public float y;
    public float z;
    public float w;

    @Override
    public byte[] getBytes(){
        byte[] byteArray= new byte[16];

        int intBits =  Float.floatToIntBits(x);
        byteArray[3] =  (byte) (intBits >> 24);
        byteArray[2] =  (byte) (intBits >> 16);
        byteArray[1] =  (byte) (intBits >> 8);
        byteArray[0] =  (byte) (intBits);

        intBits =  Float.floatToIntBits(y);
        byteArray[7] =  (byte) (intBits >> 24);
        byteArray[6] =  (byte) (intBits >> 16);
        byteArray[5] =  (byte) (intBits >> 8);
        byteArray[4] =  (byte) (intBits);

        intBits =  Float.floatToIntBits(z);
        byteArray[11] =  (byte) (intBits >> 24);
        byteArray[10] =  (byte) (intBits >> 16);
        byteArray[9] =  (byte) (intBits >> 8);
        byteArray[8] =  (byte) (intBits);

        intBits =  Float.floatToIntBits(w);
        byteArray[15] =  (byte) (intBits >> 24);
        byteArray[14] =  (byte) (intBits >> 16);
        byteArray[13] =  (byte) (intBits >> 8);
        byteArray[12] =  (byte) (intBits);



        return byteArray;
    }

    @Override
    public int getSize(){
        return 12;
    }

    @Override
    public void parse(byte[]_data){
        // azimut_ = Float.intBitsToFloat();
        // azimut_ = Float.intBitsToFloat();
        // azimut_ = Float.intBitsToFloat();
    }
}