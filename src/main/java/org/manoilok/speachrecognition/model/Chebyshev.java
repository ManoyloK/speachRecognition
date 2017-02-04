package main.java.org.manoilok.speachrecognition.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Modest on 23.11.2016.
 */
public class Chebyshev {
    private final int N=256;


    private double cm (List<Double> s, int k,int step){
        double PI= Math.PI;
        double c=0;
        for (int i=step*N;i<(step+1)*N;i++){
            c+=s.get(i)*Math.cos((PI*k*(i+0.5f))/N);
        }
        return Math.sqrt(2.f/N)*c;
    }

    private double t (List<Double> s, int k,int step){

        double c = cm(s , k , step);
        return Math.sqrt(c*c);
    }
    public List<List<Double>> chebyshev(List<Double> s){
        int spectrCount = s.size()/N;
        List<List<Double>> ck =new ArrayList<>();
        for (  int i=0; i< spectrCount; i++){
            ck.add(new ArrayList<>());
            for (int j=0; j<N; j++)
                ck.get(i).add(t(s,j,i));
        }
        return ck;
    }
}
