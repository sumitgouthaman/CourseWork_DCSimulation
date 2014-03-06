package server_classes;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author sumit
 */
public class ServerLoadsUpdateHandler implements Runnable {

    String data = null;
    ServerLoad[] serverLoads = null;

    public ServerLoadsUpdateHandler(String data, ServerLoad[] serverLoads) {
        this.data = data;
        this.serverLoads = serverLoads;
    }

    public void run() {
        JSONObject update = new JSONObject(data);
        JSONArray loads = update.getJSONArray("loads");
        for (int i = 0; i < loads.length(); i++) {
            JSONObject load = loads.getJSONObject(i);
            int newLoad = load.getInt("load");
            long timestamp = load.getLong("timestamp");
            serverLoads[i].set(newLoad, timestamp);
        }
    }
}
