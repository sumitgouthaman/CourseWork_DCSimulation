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
public class BasicServerThread implements Runnable {
    
    Socket s;
    PrintWriter pw;
    ArrayList<Video> videos;
    
    public BasicServerThread(Socket s, ArrayList<Video> videos) {
        this.s = s;
        try {
            this.pw = new PrintWriter(s.getOutputStream());
        } catch (IOException ioe) {
            System.err.println("Error creating PrintWriter from Socket in BasicServerThread");
            ioe.printStackTrace();
        }
        this.videos = videos;
    }
    
    public void run() {
        ClusterTransmitTools.transmitCluster(videos, pw);
    }
}
