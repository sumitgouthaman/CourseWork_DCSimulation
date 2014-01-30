package server_classes;

import common_classes.Video;
import common_classes.VideoCache;
import java.net.Socket;

/**
 *
 * @author sumit
 */
public class VideoServeThread implements Runnable {

    Video[] videos;
    VideoCache cache;
    Socket s;
    int videoID;
    String mainServerIP;
    int mainServerPort;

    public VideoServeThread(Socket s, int videoID, Video[] videos, VideoCache cache, String mainServerIP, int mainServerPort) {
        this.s = s;
        this.videos = videos;
        this.cache = cache;
        this.videoID = videoID;
        this.mainServerIP = mainServerIP;
        this.mainServerPort = mainServerPort;
    }

    public void run() {
        System.out.printf("Request for video %d received%n", videoID);
        Video target = cache.get(videoID);
        if (target != null) {
            target.transmitWithHeader(s);
            System.out.printf("Video %d served from cache%n", videoID);
            return;
        }
        System.out.println("Accessing from disk. Delay of 500 ms.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            System.err.println("Error sleeping before disk access");
            ie.printStackTrace();
        }
        for (Video v : videos) {
            if (v.videoID == videoID) {
                target = v;
                break;
            }
        }
        if (target != null) {
            target.transmitWithHeader(s);
            System.out.printf("Video %d served from disk%n", videoID);
            cache.put(target);
            return;
        }
        System.out.println("Not found on Disk. Will ask main server. Delay 500 ms.");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            System.err.println("Error sleeping before Main Server access");
            ie.printStackTrace();
        }
        target = VideoRequester.fetchVideo(videoID, mainServerIP, mainServerPort);
        if (target != null) {
            System.out.println("Video received from main server");
            target.transmitWithHeader(s);
            System.out.printf("Video %d served from main server%n", videoID);
            cache.put(target);
            return;
        }
        System.out.println("Couldn't find video anywhere!");
    }
}
