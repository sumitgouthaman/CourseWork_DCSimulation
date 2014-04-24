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
    ServerMonitor sm = null;
    ServerLoad[] serverLoads = null;

    public VideoServeThread(Socket s, int videoID, Video[] videos, VideoCache cache, String mainServerIP, int mainServerPort, ServerLoad[] serverLoads) {
        this.s = s;
        this.videos = videos;
        this.cache = cache;
        this.videoID = videoID;
        this.mainServerIP = mainServerIP;
        this.mainServerPort = mainServerPort;
        this.serverLoads = serverLoads;
    }

    public void setServerMonitor(ServerMonitor sm) {
        this.sm = sm;
    }

    public void run() {
        System.out.printf("Request for video %d received%n", videoID);
        if (serverLoads != null) {
            double normalTh, overloadTh;
            normalTh = ServerLoad.getNormalThreshold(serverLoads);
            overloadTh = ServerLoad.getOverloadThreshold(serverLoads);
            double currentTh = sm.getLoad();
            if (currentTh > overloadTh) {
                int index = ServerLoad.getUnderloadedServer(serverLoads);
                if (index == -1) {
                    System.out.printf("Couldn't find server to transfer request for video %d%n", videoID);
                } else {
                    String transferIP = serverLoads[index].IP;
                    int transferPort = serverLoads[index].port;
                    Video target = VideoRequester.fetchVideo(videoID, transferIP, transferPort);
                    if (target != null) {
                        System.out.printf("Request for Video %d transferred to server %d%n", videoID, index + 1);
                        target.transmitWithHeader(s);
                        return;
                    }
                }
            }
        }

        if (sm != null) {
            sm.newRequest();
        }

        Video target = cache.get(videoID);
        if (target != null) {
            target.transmitWithHeader(s);
            System.out.printf("Video %d served from cache%n", videoID);
            if (sm != null) {
                sm.requestHandled();
            }
            return;
        }
        System.out.printf("Accessing video %d from disk. Delay of 500 ms.%n", videoID);
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
            if (sm != null) {
                sm.requestHandled();
            }
            return;
        }
        System.out.printf("Video %d Not found on Disk. Will ask main server. Delay 500 ms.%n", videoID);
        try {
            Thread.sleep(500);
        } catch (InterruptedException ie) {
            System.err.println("Error sleeping before Main Server access");
            ie.printStackTrace();
        }
        target = VideoRequester.fetchVideo(videoID, mainServerIP, mainServerPort);
        if (target != null) {
            System.out.printf("Video %d received from main server%n", videoID);
            target.transmitWithHeader(s);
            System.out.printf("Video %d served from main server%n", videoID);
            cache.put(target);
            if (sm != null) {
                sm.requestHandled();
            }
            return;
        }
        System.out.printf("Couldn't find video anywhere!%n", videoID);
        if (sm != null) {
            sm.requestHandled();
        }
    }
}
