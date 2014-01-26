package common_classes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author sumit
 */
public class VideosLoader {

    public static Video[] loadVideos(String fileName) {
        File file = new File(fileName);
        return loadVideos(file);
    }

    public static Video[] loadVideos(File file) {
        try {
            Scanner sc = new Scanner(new FileReader(file));
            ArrayList<Video> videos = new ArrayList<Video>();
            while(sc.hasNextLine()){
                String temp = sc.nextLine();
                if(!temp.contains(";")){
                    continue;
                }
                String[] temparr = temp.split(";");
                Video v = new Video(temparr[0],temparr[1]);
                videos.add(v);
            }
            return videos.toArray(new Video[0]);
        } catch (FileNotFoundException fnfe) {
            System.err.println("File not found in VideosLoader");
            fnfe.printStackTrace();
        }
        return null;
    }
    
    public static Video[] loadSortedVideos(String fileName){
        File file = new File(fileName);
        return loadSortedVideos(file);
    }
    
    public static Video[] loadSortedVideos(File file){
        Video[] videos = loadVideos(file);
        Arrays.sort(videos);
        return videos;
    }
}
