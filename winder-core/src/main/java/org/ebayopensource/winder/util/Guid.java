/**
 * Copyright (c) 2016 eBay Software Foundation. All rights reserved.
 *
 * Licensed under the MIT license.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.ebayopensource.winder.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public final class Guid {
    private final String staticGUID;
    private final String paddedStaticGUID;
    private final String isoPaddedStaticGUID;
    private final AtomicInteger m_counter = new AtomicInteger();

    public Guid() {
        String inetAddrHexString = this.createInetAddressHashString();
        String objAddrHexString = this.createObjectAddressHashString();
        this.staticGUID = this.genStaticGuid(inetAddrHexString, objAddrHexString);
        this.paddedStaticGUID = this.genPaddedStaticGuid(inetAddrHexString, objAddrHexString);
        this.isoPaddedStaticGUID = this.genISOStaticUUID(this.paddedStaticGUID);
    }

    private String createInetAddressHashString() {
        byte[] inetAddrBytes;
        try {
            inetAddrBytes = InetAddress.getLocalHost().getAddress();
        } catch (UnknownHostException e) {
            inetAddrBytes = new byte[] { 127, 0, 0, 1};
        }
        int addrInt = (inetAddrBytes[0] & 255) << 24 | (inetAddrBytes[1] & 255) << 16 | (inetAddrBytes[2] & 255) << 8 | inetAddrBytes[3] & 255;
        return Integer.toHexString(addrInt);
    }

    private String createObjectAddressHashString() {
        int objAddr = System.identityHashCode(this);
        return Integer.toHexString(objAddr >>> 12);
    }

    private String genStaticGuid(String inetAddrHexString, String objAddrHexString) {
        return "." + inetAddrHexString + "." + objAddrHexString + ".";
    }

    private String genPaddedStaticGuid(String inetAddrHexString, String objAddrHexString) {
        return this.padToMinimumLength(inetAddrHexString, 8) + this.padToMinimumLength(objAddrHexString, 5);
    }

    private String genISOStaticUUID(String paddedGuid) {
        return paddedGuid.substring(0, 1) +
                "-" +
                paddedGuid.substring(1, 5) +
                "-" +
                paddedGuid.substring(5, 9) +
                "-" +
                paddedGuid.substring(9);
    }

    public String nextGUID() {
        StringBuilder buff = new StringBuilder();
        long millis = System.currentTimeMillis();
        millis &= 17592186044415L;
        buff.append(this.padToMinimumLength(Long.toHexString(millis), 11));
        buff.append(this.staticGUID);
        buff.append(Integer.toHexString(this.nextCounter()));
        return buff.toString();
    }

    public String nextPaddedGUID() {
        StringBuilder buff = new StringBuilder(32);
        long millis = System.currentTimeMillis();
        millis &= 17592186044415L;
        String milliStr = this.padToMinimumLength(Long.toHexString(millis), 11);
        buff.append(milliStr.substring(3));
        buff.append(milliStr.substring(0, 3));
        buff.append(this.paddedStaticGUID);
        buff.append(Integer.toHexString(this.nextCounter()));
        return buff.toString();
    }

    public String nextISOGUID() {
        StringBuilder buff = new StringBuilder(36);
        long millis = System.currentTimeMillis();
        millis &= 17592186044415L;
        String timeStr = this.padToMinimumLength(Long.toHexString(millis), 11);
        buff.append(timeStr.substring(0, 8));
        buff.append("-");
        buff.append(timeStr.substring(8));
        buff.append(this.isoPaddedStaticGUID);
        buff.append(Integer.toHexString(this.nextCounter()));
        return buff.toString();
    }


    private String padToMinimumLength(String stringToPad, int requiredMinimumLength) {
        int origStringLength = stringToPad.length();
        if(origStringLength >= requiredMinimumLength) {
            return stringToPad;
        } else {
            StringBuilder buff = new StringBuilder(requiredMinimumLength);

            for(int i = origStringLength; i < requiredMinimumLength; ++i) {
                buff.append('0');
            }

            buff.append(stringToPad);
            return buff.toString();
        }
    }

    private int nextCounter() {
        return this.m_counter.decrementAndGet() | -2147483648;
    }

    public static void main(String[] args) throws IOException {
        Guid t = new Guid();
        int numTests = 100000;
        long startIsoGUID = System.currentTimeMillis();

        for(int startNextGUID = 0; startNextGUID < numTests; ++startNextGUID) {
            t.nextISOGUID();
        }

        System.out.println("nextISOGUID=" + (System.currentTimeMillis() - startIsoGUID));
        long var10 = System.currentTimeMillis();

        for(int startNextPaddedGUID = 0; startNextPaddedGUID < numTests; ++startNextPaddedGUID) {
            t.nextGUID();
        }

        System.out.println("nextGUID=" + (System.currentTimeMillis() - var10));
        long var11 = System.currentTimeMillis();

        for(int t2 = 0; t2 < numTests; ++t2) {
            t.nextPaddedGUID();
        }

        System.out.println("nextPaddedGUID=" + (System.currentTimeMillis() - var11));
        System.out.println("UNP GUID: " + t.nextGUID());
        System.out.println("PAD GUID: " + t.nextPaddedGUID());
        System.out.println("ISO GUID: " + t.nextISOGUID());
        Guid var12 = new Guid();
        System.out.println("t2.UNP GUID: " + var12.nextGUID());
    }
}
