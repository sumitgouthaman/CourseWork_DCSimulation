package server_classes;

import common_classes.Video;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author sumit
 */
public class ClusterTransmitTools {

    public static void transmitCluster(ArrayList<Video> cluster, PrintWriter pw) {
        pw.println("START-CLUSTER");
        for (Video v : cluster) {
            v.transmit(pw);
        }
        pw.println("END-CLUSTER");
    }

    public static void transmitCluster(ArrayList<Video> cluster, Socket s) {
        try {
            PrintWriter pw = new PrintWriter(s.getOutputStream());
            transmitCluster(cluster, pw);
        } catch (IOException ioe) {
            System.err.println("Error creating PrintWriter from Socket in ClusterTransmitTools");
            ioe.printStackTrace();
        }
    }
}
