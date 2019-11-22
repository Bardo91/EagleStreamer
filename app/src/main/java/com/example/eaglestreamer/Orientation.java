package com.example.eaglestreamer;

public class Orientation implements Byteable {
    public float azimut_;
    public float pitch_;
    public float roll_;

    @Override
    public byte[] getBytes(){
        byte[] byteArray= new byte[12];

        int intBits =  Float.floatToIntBits(azimut_);
        byteArray[3] =  (byte) (intBits >> 24);
        byteArray[2] =  (byte) (intBits >> 16);
        byteArray[1] =  (byte) (intBits >> 8);
        byteArray[0] =  (byte) (intBits);

        intBits =  Float.floatToIntBits(pitch_);
        byteArray[7] =  (byte) (intBits >> 24);
        byteArray[6] =  (byte) (intBits >> 16);
        byteArray[5] =  (byte) (intBits >> 8);
        byteArray[4] =  (byte) (intBits);

        intBits =  Float.floatToIntBits(roll_);
        byteArray[11] =  (byte) (intBits >> 24);
        byteArray[10] =  (byte) (intBits >> 16);
        byteArray[9] =  (byte) (intBits >> 8);
        byteArray[8] =  (byte) (intBits);

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