/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_classes;

import common_classes.Video;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author sumit
 */
public class ServerLoadsBroadcaster extends Thread {

    ServerLoad[] serverLoads;
    int ownID;
    ServerMonitor sm;

    public ServerLoadsBroadcaster(ServerLoad[] serverLoads, int ownID, ServerMonitor sm) {
        this.serverLoads = serverLoads;
        this.ownID = ownID;
        this.sm = sm;
    }

    public void run() {
        try {
            Thread.sleep(15000);
            while (true) {
                Thread.sleep(1000 + (int) (2000.0 * Math.random()));
                serverLoads[ownID].load = sm.getLoad();
                serverLoads[ownID].timestamp = System.currentTimeMillis();
                JSONArray serverLoadsArr = new JSONArray();
                for (int sl = 0; sl < serverLoads.length; sl++) {
                    JSONObject serverLoadOb = new JSONObject();
                    serverLoadOb.put("load", serverLoads[sl].load);
                    serverLoadOb.put("timestamp", serverLoads[sl].timestamp);
                    serverLoadsArr.put(serverLoadOb);
                }
                JSONObject update = new JSONObject();
                update.put("loads", serverLoadsArr);
                for (int sl = 0; sl < serverLoads.length; sl++) {
                    if (sl == ownID) {
                        continue;
                    }
                    Socket s = new Socket(serverLoads[sl].IP, serverLoads[sl].port);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
                    pw.println("LOAD-UPDATES");
                    pw.println(update.toString());
                    pw.println("END-REQUEST");
                    s.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
