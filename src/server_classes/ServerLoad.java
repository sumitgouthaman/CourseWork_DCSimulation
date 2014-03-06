package server_classes;

/**
 *
 * @author sumit
 */
public class ServerLoad {

    public int load = 0;
    public long timestamp = 0l;
    String IP = null;
    int port = 0;

    public ServerLoad(int load, long timestamp) {
        set(load, timestamp);
    }

    public void setIPPort(String IP, int port) {
        this.IP = IP;
        this.port = port;
    }

    public synchronized void set(int load, long timestamp) {
        if (timestamp > this.timestamp) {
            this.load = load;
            this.timestamp = timestamp;
        }
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
