package client_classes;

import common_classes.Video;
import java.util.Scanner;
import server_classes.VideoRequester;

/**
 *
 * @author sumit
 */
public class ClientRequester {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter <IP port videoID> to make a request:");
        while (true) {
            String IP = sc.next();
            int port = sc.nextInt();
            int videoID = sc.nextInt();
            long start = System.currentTimeMillis();
            Video target = VideoRequester.fetchVideo(videoID, IP, port);
            long end = System.currentTimeMillis();
            long delay = end - start;
            System.out.println("Fetched video: " + target + " with delay: " + delay + " ms.");
        }
    }
}
