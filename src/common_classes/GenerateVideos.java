package common_classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author sumit
 * 
 * Just a basic class to generate videos.txt and populate it with random values
 * 
 * Sizes are generated in range 400 - 1000
 */
public class GenerateVideos {

    public static void main(String[] args) {
        File videosFile = new File("videos.txt");
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(videosFile), true);
            for (int i = 1; i <= 100; i++) {
                int size = ((int) (Math.random() * 600)) + 400;
                pw.println(i + ";" + size);
            }
            pw.close();
        } catch (IOException ioe) {
            System.err.println("Error creating videos file");
            ioe.printStackTrace();
        }
    }
}
