/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client_classes;

import common_classes.Video;
import java.util.Scanner;
import server_classes.VideoRequester;

/**
 *
 * @author sumit
 */
public class LoopedRequester {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter <IP port> to make looped request:");
        final String IP = sc.next();
        final int port = sc.nextInt();
        while (true) {
            Thread t = new Thread(new Runnable() {
                public void run() {
                    int videoID = (int) (Math.random() * 100.0) + 1;
                    long start = System.currentTimeMillis();
                    Video target = VideoRequester.fetchVideo(videoID, IP, port);
                    long end = System.currentTimeMillis();
                    long delay = end - start;
                    System.out.println("Fetched video: " + target + " with delay: " + delay + " ms.");
                }
            });
            t.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                System.err.println("Error sleeping inn looped requester");
                ie.printStackTrace();
            }
        }
    }
}
