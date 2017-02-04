package main.java.org.manoilok.speachrecognition.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Modest on 26.10.2016.
 */
public class LowPassFilter {
    private int N=7;
    private double FCP=0.1d;


    public LowPassFilter(int n, double FCP) {
        N = n;
        this.FCP = FCP;
    }

    public List<Double>applyLPfilter(List<Double> band){
        List<Double> filtredSpectr = new ArrayList<>();

        for (int l=0;l<band.size();l++) {
            double y=0;
            for (int k=-N;k<=N;k++){
                if (l-k<0){
                    y+=c(k)*band.get(0);
                }
                else
                if (l-k>band.size()-1){
                    y+=c(k)*band.get(band.size()-1);
                }
                else
                    y+=c(k)*band.get(l-k);
            }
            filtredSpectr.add(y);
        }
        return filtredSpectr;
    }
    public List<Double>applyLPfilter2(List<Double> band){
        int N1 =256;
        List<Double> filtredSpectr = new ArrayList<>();

        for (int l=0;l+N1<band.size();l+=N1) {
            double y=0;

            for (int k=l;k<l+N1-1;k++) {
                y= Math.max(y,Math.abs(band.get(k)));
            }


            for (int i=0;i<N1;i++){
                filtredSpectr.add(y);
            }

        }
        return filtredSpectr;
    }
    private double c (int k){
        double PI = Math.PI;
        return   k==0?2*FCP:((1/(k*PI))*Math.sin(2*k*PI*FCP));
    }
}
