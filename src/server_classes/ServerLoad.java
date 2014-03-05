/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_classes;

/**
 *
 * @author sumit
 */
public class ServerLoad {

    public int load = 0;
    public long timestamp = 0l;

    public ServerLoad(int load, long timestamp) {
        this.load = load;
        this.timestamp = timestamp;
    }

    public synchronized double getNormalThreshold(ServerLoad[] loads) {
        int n = loads.length;
        int total = 0;
        for (ServerLoad sl : loads) {
            total += sl.load;
        }
        return ((double) total / (double) n);
    }

    public synchronized double getOverloadThreshold(ServerLoad[] loads) {
        int n = loads.length;
        int total = 0;
        for (ServerLoad sl : loads) {
            total += sl.load;
        }
        return ((double) total / (double) n) * 1.3;
    }
}
