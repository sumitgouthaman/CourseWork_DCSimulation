package server_classes;

import common_classes.Video;
import common_classes.VideosLoader;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class MainServer {
    public static void main(String[] args){
        String videosFileName = null;
        Scanner sc = new Scanner(System.in);
        if(args.length>0){
            videosFileName = args[0];
        }else{
            System.out.println("Enter file to load videos: ");
            videosFileName = sc.nextLine();
        }
        Video[] videos = VideosLoader.loadSortedVideos(videosFileName);
        System.out.println(Arrays.toString(videos));
    }
}
