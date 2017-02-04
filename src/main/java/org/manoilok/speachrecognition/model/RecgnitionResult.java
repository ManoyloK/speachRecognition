package main.java.org.manoilok.speachrecognition.model;

/**
 * Created by Modest on 19.10.2016.
 */
public class RecgnitionResult {

    private String filename;
    private  double distance;

    public String getFilename() {
        return filename;
    }
    public double getDistance() {
        return distance;
    }

    public RecgnitionResult(String filename, double distance) {
        this.filename = filename;
        this.distance = distance;
    }
}
