package server_classes;

import common_classes.ClusteringTools;
import common_classes.CommonUtils;
import common_classes.IPFunctions;
import common_classes.Video;
import common_classes.VideoCache;
import common_classes.VideosLoader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class MainServer {

    static int mainServerPort = 0;

    public static void main(String[] args) {
        String videosFileName = null;
        int noOfClusters = 0;
        Scanner sc = new Scanner(System.in);
        if (args.length > 0) {
            videosFileName = args[0];
        } else {
            System.out.println("Enter file to load videos: ");
            videosFileName = sc.nextLine();
        }
        if (args.length > 1) {
            noOfClusters = Integer.parseInt(args[1]);
        } else {
            System.out.println("Enter no of clusters: ");
            noOfClusters = Integer.parseInt(sc.nextLine());
        }
        CommonUtils.printDelimiter();
        System.out.printf("Creating %d clusters%n", noOfClusters);
        CommonUtils.printDelimiter();

        Video[] videos = VideosLoader.loadVideos(videosFileName);
        ArrayList<ArrayList<Video>> clusters = ClusteringTools.clusterVideos(videos, noOfClusters);
        int n = 0;
        for (ArrayList<Video> cluster : clusters) {
            int sum = 0;
            for (Video v : cluster) {
                sum += v.size;
            }
            System.out.printf("CLUSTER %d:%nSize = %d%n", n + 1, sum);
            System.out.println("Videos in cluster:");
            for (Video v : cluster) {
                System.out.printf("%d ", v.videoID);
            }
            System.out.println();
            CommonUtils.printDelimiter();
            n++;
        }

        try {
            System.out.println("Which port to use for this Main Server?");
            int port = sc.nextInt();
            mainServerPort = port;
            ServerSocket serverSocket = new ServerSocket(port);
            String IP = IPFunctions.getFirstNonLoopbackAddress().toString();
            System.out.println("Main Server running at:");
            System.out.printf("IP Address: %s%n", IP);
            System.out.printf("PORT Address: %d%n", port);
            CommonUtils.printDelimiter();
            for (int proxies = 0; proxies < noOfClusters; proxies++) {
                Socket s = serverSocket.accept();
                Thread basicServerThread = new Thread(new BasicServerTransmitThread(s, clusters.get(proxies)));
                basicServerThread.start();
            }

            System.out.println("Enter capacity for cache: ");
            int cacheCapacity = sc.nextInt();
            VideoCache cache = new VideoCache(cacheCapacity);
            while (true) {
                Socket s = serverSocket.accept();
                Scanner requestSocketScanner = new Scanner(s.getInputStream());
                requestSocketScanner.nextLine();
                int requestVideoID = Integer.parseInt(requestSocketScanner.nextLine());
                requestSocketScanner.nextLine();
                VideoServeThread vst = new VideoServeThread(s, requestVideoID, videos, cache, null, 0);
                Thread t = new Thread(vst);
                t.start();
            }
        } catch (Exception e) {
            System.err.println("Error while serving requests");
            e.printStackTrace();
        }
    }
}
