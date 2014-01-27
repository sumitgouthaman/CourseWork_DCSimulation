package server_classes;

import common_classes.CommonUtils;
import common_classes.IPFunctions;
import common_classes.Video;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class ProxyServer {

    static Video[] videos;

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter port to use for this server:");
        int port = sc.nextInt();
        System.out.println("Enter IP of Main Server:");
        String mainServerIP = sc.next();
        System.out.println("Enter PORT of Main Server:");
        int mainServerPort = sc.nextInt();
        String IP = IPFunctions.getFirstNonLoopbackAddress().toString();
        System.out.println("Proxy server running at:");
        System.out.printf("IP Address: %s%n", IP);
        System.out.printf("PORT Address: %d%n", port);
        CommonUtils.printDelimiter();
        System.out.println("Contacting Main Server....");
        try {
            Socket s = new Socket(mainServerIP, mainServerPort);
            System.out.println("Connected to main server....");
            Scanner mainServerScanner = new Scanner(s.getInputStream());

            ArrayList<Video> videosList = null;
            while (mainServerScanner.hasNextLine()) {
                String response = mainServerScanner.nextLine();
                if (response.equals("START-CLUSTER")) {
                    System.out.println("Receiving video files");
                    videosList = new ArrayList<Video>();
                } else if (response.equals("END-CLUSTER")) {
                    if (videosList != null) {
                        videos = videosList.toArray(new Video[0]);
                        videosList.clear();
                        videosList = null;
                        System.out.println("Received all videos meant for this proxy server");
                        long sum = 0;
                        for (Video v : videos) {
                            sum += v.size;
                        }
                        System.out.println("Total size: " + sum);
                        CommonUtils.printDelimiter();
                    }
                    break;
                } else {
                    if (response.contains(";")) {
                        String[] temp = response.split(";");
                        Video newVideo = new Video(temp[0], temp[1]);
                        videosList.add(newVideo);
                        System.out.println("Received Video, " + newVideo);
                    }
                }
            }

            s.close();
        } catch (IOException ioe) {
            System.err.println("Error with initial payload trasfer socket in Proxy Server");
            ioe.printStackTrace();
        }
    }
}
