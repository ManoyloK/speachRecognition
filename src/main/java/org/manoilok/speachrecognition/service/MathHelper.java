package main.java.org.manoilok.speachrecognition.service;

import java.util.List;

/**
 * Created by Modest on 09.09.2016.
 */
class MathHelper {

    public static double dispersion(List<Double> data){
        long d= 0;
        for (double bt:data){
            d+=(bt*bt)/data.size();
        }
        return  Math.sqrt(d);
    }
    public static double threshold(List<Double> data){
        return (1f/3f)*dispersion(data);
    }

}
