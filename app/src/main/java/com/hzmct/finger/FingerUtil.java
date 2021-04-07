package com.hzmct.finger;

/**
 * @author Woong on 2020/7/3
 * @website http://woong.cn
 */
public class FingerUtil {
    public static byte[] handshake() {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 3;
        serialPacket.confirmCode = (byte)0x35;
        byte[] infos = {};
        return serialPacket.encodePacket(infos);
    }

    public static byte[] collect() {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 3;
        serialPacket.confirmCode = (byte)0x01;
        byte[] infos = {};
        return serialPacket.encodePacket(infos);
    }

    public static byte[] saveId(int count) {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 4;
        serialPacket.confirmCode = (byte)0x02;
        byte[] infos = {(byte)(count & 0xFF)};
        return serialPacket.encodePacket(infos);
    }

    public static byte[] compound() {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 3;
        serialPacket.confirmCode = (byte)0x05;
        byte[] infos = {};
        return serialPacket.encodePacket(infos);
    }

    public static byte[] saveCompound() {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 6;
        serialPacket.confirmCode = (byte)0x06;
        byte[] infos = {0x01, 0x00, 0x00};
        return serialPacket.encodePacket(infos);
    }

    public static byte[] query() {
        FingerPacket serialPacket = new FingerPacket();
        serialPacket.packageType = 1;
        serialPacket.dataLens = 8;
        serialPacket.confirmCode = (byte)0x04;
        byte[] infos = {0x01, 0x00, 0x00, 0x00, 0x63};
        return serialPacket.encodePacket(infos);
    }

    public static FingerPacket result(byte[] resultBytes) {
        FingerPacket resultPacket = new FingerPacket();
        resultPacket.decodePacket(resultBytes);

        return resultPacket;
    }
}
