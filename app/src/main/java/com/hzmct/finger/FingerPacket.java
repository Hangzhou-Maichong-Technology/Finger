package com.hzmct.finger;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;

/**
 * @author Woong on 2020/7/1
 * @website http://woong.cn
 */
public class FingerPacket {
    public byte[] head = {(byte)0xef, 0x01};
    public byte[] device = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
    public byte packageType = 0x00;
    public int dataLens = 0;
    public byte confirmCode = 0x00;
    public byte[] sumCode = new byte[2];

    public void decodePacket(byte[] packet) {
        if (packet.length < 12) {
            LogUtils.e("receiver error packet == ");
            return;
        }

        confirmCode = packet[9];
    }

    public byte[] encodePacket(byte[] infos) {
        int fixLen = head.length + device.length + 1 + 2;
        int totalLens = fixLen + dataLens;
        byte[] packet = new byte[totalLens];

        packet[0] = head[0];
        packet[1] = head[1];
        packet[2] = device[0];
        packet[3] = device[1];
        packet[4] = device[2];
        packet[5] = device[3];
        packet[6] = packageType;
        packet[7] = (byte)((dataLens >> 8) & 0xFF);
        packet[8] = (byte)(dataLens & 0xFF);
        packet[9] = confirmCode;
        System.arraycopy(infos, 0, packet, fixLen + 1, infos.length);

        byte[] calSumBytes = new byte[dataLens + 2 + 1];
        System.arraycopy(packet, 6, calSumBytes, 0, calSumBytes.length - 2);
        sumCode = calCheckSum(calSumBytes);
        packet[totalLens - 2] = sumCode[0];
        packet[totalLens - 1] = sumCode[1];

        LogUtils.i("encodePacket === " + ConvertUtils.bytes2HexString(packet));
        return packet;
    }

    /**
     * 计算校验和
     * @param input
     * @return
     */
    public static byte[] calCheckSum(byte[] input) {
        int len = input.length;
        int sum = 0;
        for (int i = 0; i < len; i++) {
            //去除byte符号位
            sum += input[i] & 0xFF;
        }

        byte[] crc = new byte[2];
        crc[0] = (byte) ((sum & 0xff00) >> 8);
        crc[1] = (byte) (sum & 0x00ff);

        return crc;
    }
}
