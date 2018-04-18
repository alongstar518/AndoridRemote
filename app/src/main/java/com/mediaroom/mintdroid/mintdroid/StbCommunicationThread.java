package com.mediaroom.mintdroid.mintdroid;

/**
 * Created by zhengnie on 4/23/17.
 */

import clientPairing.Pairing;
import clientPairing.HttpCompanion;


public class StbCommunicationThread extends Thread
{
    private String ip;
    private String key;
    private final String cmd = "op=remotekey&key=";
    private final String testPairStr = "ABCD";
    private final String testDeviceId = "1234";

    public StbCommunicationThread(String Ip,String Key)
    {
        ip = Ip;
        key = Key;
    }


    public void run()
    {
        try {
            Pairing p = new Pairing(testDeviceId, testPairStr, ip, ip);
            HttpCompanion httpCompanion = new HttpCompanion(p);
            httpCompanion.Send(cmd + key);
        }
        catch (Exception e)
        {
           System.out.println(e.getMessage());
        }
    }

}
