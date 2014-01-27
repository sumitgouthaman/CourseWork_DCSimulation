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
public class BasicServerTransmitThread implements Runnable {

    Socket s;
    PrintWriter pw;
    ArrayList<Video> videos;

    public BasicServerTransmitThread(Socket s, ArrayList<Video> videos) {
        this.s = s;
        try {
            this.pw = new PrintWriter(s.getOutputStream(), true);
        } catch (IOException ioe) {
            System.err.println("Error creating PrintWriter from Socket in BasicServerThread");
            ioe.printStackTrace();
        }
        this.videos = videos;
    }

    public BasicServerTransmitThread(PrintWriter pw, ArrayList<Video> videos) {
        this.pw = pw;
        this.videos = videos;
    }

    public void run() {
        ClusterTransmitTools.transmitCluster(videos, pw);
        pw.close();
        if (s != null) {
            try {
                s.close();
            } catch (IOException ioe) {
                System.err.println("Error closing socket after basic server transmit");
                ioe.printStackTrace();
            }
        }
    }
}
