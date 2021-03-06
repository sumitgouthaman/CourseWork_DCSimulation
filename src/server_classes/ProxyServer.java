package server_classes;

import common_classes.CommonUtils;
import common_classes.IPFunctions;
import common_classes.Video;
import common_classes.VideoCache;
import java.io.IOException;
import java.net.ServerSocket;
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
        System.out.println("Enter capacity for cache: ");
        int cacheCapacity = sc.nextInt();
        VideoCache cache = new VideoCache(cacheCapacity);
        System.out.println("How many proxy servers are there: ");
        int noOfProxyServers = sc.nextInt();
        System.out.println("ID of this server: ");
        int thisServerID = sc.nextInt() - 1;
        ServerLoad[] otherServerLoads = new ServerLoad[noOfProxyServers];
        System.out.println("Enter <IP> <PORT> of other servers:");
        for (int i = 0; i < noOfProxyServers; i++) {
            otherServerLoads[i] = new ServerLoad();
            if (i == thisServerID) {
                otherServerLoads[i].setIPPort(IP, port);
                continue;
            }
            String otherIP = sc.next();
            int otherPort = sc.nextInt();
            otherServerLoads[i].setIPPort(otherIP, otherPort);
        }
        CommonUtils.printDelimiter();
        ServerMonitor sm = new ServerMonitor();
        sm.setServerName("Proxy Server " + (thisServerID + 1));
        sm.setIPAddressPort(IP, port);
        cache.setServerMonitor(sm);
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
            sm.setVisible(true);
            Thread loadsBroadcaster = new ServerLoadsBroadcaster(otherServerLoads, thisServerID, sm);
            loadsBroadcaster.start();
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                while (true) {
                    s = serverSocket.accept();
                    Scanner requestSocketScanner = new Scanner(s.getInputStream());
                    String HEAD = requestSocketScanner.nextLine();
                    if (HEAD.equalsIgnoreCase("REQUEST-VIDEO")) {
                        int requestVideoID = Integer.parseInt(requestSocketScanner.nextLine());
                        requestSocketScanner.nextLine();
                        VideoServeThread vst = new VideoServeThread(s, requestVideoID, videos, cache, mainServerIP, mainServerPort, otherServerLoads);
                        vst.setServerMonitor(sm);
                        Thread t = new Thread(vst);
                        t.start();
                    } else if (HEAD.equalsIgnoreCase("LOAD-UPDATES")) {
                        String data = requestSocketScanner.nextLine();
                        requestSocketScanner.nextLine();
                        requestSocketScanner.close();
                        ServerLoadsUpdateHandler sluh = new ServerLoadsUpdateHandler(data, otherServerLoads);
                        sluh.setServerMonitor(sm);
                        Thread t = new Thread(sluh);
                        t.start();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error while serving requests");
                e.printStackTrace();
            }
        } catch (IOException ioe) {
            System.err.println("Error with initial payload trasfer socket in Proxy Server");
            ioe.printStackTrace();
        }
    }
}
