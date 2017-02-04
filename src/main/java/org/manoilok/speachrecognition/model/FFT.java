package main.java.org.manoilok.speachrecognition.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Modest on 13.09.2016.
 */
public class FFT {
    private final int N=256;
    private final double PI= Math.PI;

    private double a (List<Double> s, int k,int step){
        double a=0;
        for (int i=step*N;i<(step+1)*N;i++){
            a+=s.get(i)*Math.cos((2*PI*k*i)/N);
        }
        return 2*a/N;
    }
    private double b (List<Double> s, int k,int step){
        double b=0;
        for (int i=step*N;i<(step+1)*N;i++){
            b+=s.get(i)*Math.sin((2*PI*k*i)/N);
        }
        return 2*b/N;
    }
    private double c (List<Double> s, int k,int step){
        double a = a( s , k , step);
        double b = b( s , k , step);
        return Math.sqrt(a*a+b*b);
    }

    public List<List<Double>> fff(List<Double> s){
        int spectrCount = s.size()/N;
        List<List<Double>> ck =new ArrayList<>();
        for (  int i=0; i< spectrCount; i++){
            ck.add(new ArrayList<>());
            for (int j=0; j<N/2; j++)
                ck.get(i).add(c(s,j,i));
        }
        return ck;
    }
}
