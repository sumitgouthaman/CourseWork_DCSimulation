package common_classes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author sumit
 */
public class ClusteringTools {

    public static ArrayList<ArrayList<Video>> clusterVideos(Video[] videos, int noOfClusters) {
        if (videos.length < noOfClusters) {
            return null;
        }
        ArrayList<ArrayList<Video>> clusters = new ArrayList<ArrayList<Video>>(noOfClusters);
        for (int i = 0; i < noOfClusters; i++) {
            clusters.add(new ArrayList<Video>());
        }
        long totalSize = 0;
        for (Video v : videos) {
            totalSize += v.size;
        }
        long approxClusterSize = totalSize / noOfClusters;
        Arrays.sort(videos);
        long currentSize = 0;
        int currentCluster = 0;
        for (Video v : videos) {
            clusters.get(currentCluster).add(v);
            currentSize += v.size;
            if ((currentSize > approxClusterSize) && (currentCluster + 1 != noOfClusters)) {
                currentSize = 0;
                currentCluster++;
            }
        }
        return clusters;
    }

    public static ArrayList<ArrayList<Video>> clusterVideosKMeans(Video[] videos, int noOfClusters) {
        if (videos.length < noOfClusters) {
            return null;
        }
        ArrayList<ArrayList<Video>> clusters = new ArrayList<ArrayList<Video>>(noOfClusters);
        double[] oldMeans = new double[noOfClusters];
        double[] newMeans = new double[noOfClusters];
        for (int i = 0; i < noOfClusters; i++) {
            clusters.add(new ArrayList<Video>());
            clusters.get(i).add(videos[i]);
            newMeans[i] = videos[i].size;
        }

        while (!Arrays.equals(oldMeans, newMeans)) {
            for (int i = 0; i < noOfClusters; i++) {
                oldMeans[i] = newMeans[i];
                clusters.get(i).clear();
            }
            for (int i = 0; i < videos.length; i++) {
                double[] diffs = new double[noOfClusters];
                for (int d = 0; d < noOfClusters; d++) {
                    diffs[d] = Math.abs(videos[i].size - newMeans[d]);
                }
                double min = diffs[0];
                int minIndex = 0;
                for (int m = 1; m < noOfClusters; m++) {
                    if (diffs[m] < min) {
                        min = diffs[m];
                        minIndex = m;
                    }
                }
                clusters.get(minIndex).add(videos[i]);
            }
            for (int c = 0; c < noOfClusters; c++) {
                ArrayList<Video> cluster = clusters.get(c);
                double sum = 0;
                for (Video v : cluster) {
                    sum += v.size;
                }
                sum = sum / cluster.size();
                newMeans[c] = sum;
            }
        }

        return clusters;
    }
}
