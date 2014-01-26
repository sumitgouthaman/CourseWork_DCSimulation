package server_classes;

import common_classes.ClusteringTools;
import common_classes.CommonUtils;
import common_classes.Video;
import common_classes.VideosLoader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class MainServer {

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
            System.out.printf("CLUSTER %d:%nSize = %d%n", n, sum);
            System.out.println("Videos in cluster:");
            for (Video v : cluster) {
                System.out.printf("%d ", v.videoID);
            }
            System.out.println();
            CommonUtils.printDelimiter();
            n++;
        }
    }
}
