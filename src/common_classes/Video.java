package common_classes;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author sumit
 */
public class Video implements Comparable<Video> {

    public int videoID;
    public int size;

    public Video(int videoID, int size) {
        this.videoID = videoID;
        this.size = size;
    }

    public Video(String videoID, String size) {
        try {
            this.videoID = Integer.parseInt(videoID);
            this.size = Integer.parseInt(size);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
        }
    }

    public void transmit(PrintWriter pw) {
        try {
            Thread.sleep(size);
            pw.println(Video.serialize(this));
        } catch (InterruptedException ie) {
            System.err.println("Error while transmitting video using PrintWriter");
            ie.printStackTrace();
        }
    }

    public void transmit(Socket s) {
        try {
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            transmit(pw);
        } catch (IOException ioe) {
            System.err.println("Error creating PrintWriter from SOcket in Video class");
            ioe.printStackTrace();
        }
    }
    public void transmitWithHeader(Socket s) {
        try {
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            pw.println("START-VIDEO");
            transmit(pw);
            pw.println("END-VIDEO");
        } catch (IOException ioe) {
            System.err.println("Error creating PrintWriter from SOcket in Video class");
            ioe.printStackTrace();
        }
    }

    public static String serialize(Video v) {
        return v.videoID + ";" + v.size;
    }

    public static Video deserialize(String str) {
        String[] temp = str.split(";");
        Video v = new Video(temp[0], temp[1]);
        return v;
    }

    @Override
    public int compareTo(Video t) {
        return this.size - t.size;
    }
    
    @Override
    public String toString(){
        return String.format("{ Video ID: %d, Size: %d }", videoID, size);
    }
    
}
