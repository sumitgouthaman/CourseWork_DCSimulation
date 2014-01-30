package common_classes;

import java.util.ArrayList;

/**
 *
 * @author sumit
 */
public class VideoCache {

    public ArrayList<Video> cache;
    public int capacity;
    public int filled;

    public VideoCache(int capacity) {
        this.capacity = capacity;
        this.filled = 0;
        cache = new ArrayList<Video>();
    }

    public synchronized Video get(int videoID) {
        Video video = null;
        for (Video v : cache) {
            if (v.videoID == videoID) {
                video = v;
                cache.remove(v);
                cache.add(v);
                System.out.printf("Got video %d from cache%n", v.videoID);
            }
        }
        if (video == null) {
            System.out.printf("Video %d not found in cache%n", videoID);
        }
        return video;
    }

    public synchronized void put(Video v) {
        if (filled + v.size <= capacity) {
            cache.add(v);
            System.out.printf("Video %d added to cache%n", v.videoID);
        } else {
            if (cache.size() > 0) {
                Video firstVideo = cache.get(0);
                if (filled - firstVideo.size + v.size <= capacity) {
                    cache.remove(0);
                    cache.add(v);
                    filled = filled - firstVideo.size + v.size;
                    System.out.printf("Video %d added to cache%n", v.size);
                }
            } else {
                if (filled + v.size <= capacity) {
                    cache.add(v);
                    filled += v.size;
                    System.out.printf("Video %d added to cache%n", v.size);
                }
            }
        }
    }
}
