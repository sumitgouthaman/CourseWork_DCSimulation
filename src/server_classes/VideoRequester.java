package server_classes;

import common_classes.Video;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class VideoRequester {
    public static Video fetchVideo(int videoID, String mainServerIP, int mainServerPort){
        try{
            Socket s = new Socket(mainServerIP, mainServerPort);
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            pw.println("REQUEST-VIDEO");
            pw.println(videoID);
            pw.println("END-REQUEST");
            Scanner sc = new Scanner(s.getInputStream());
            sc.nextLine();
            String videoString = sc.nextLine();
            String[] temp = videoString.split(";");
            Video target = new Video(temp[0], temp[1]);
            sc.nextLine();
            sc.close();
            s.close();
            return target;
        }catch(Exception e){
            System.err.println("Error contacting main server");
            e.printStackTrace();
        }
        return null;
    }
}
