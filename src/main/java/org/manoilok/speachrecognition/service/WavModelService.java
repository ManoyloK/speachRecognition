package main.java.org.manoilok.speachrecognition.service;

import javafx.scene.chart.LineChart;
import main.java.org.manoilok.speachrecognition.model.WavModel;

import java.util.List;

/**
 * Created by Modest on 09.09.2016.
 */
public interface WavModelService {

    List<Double> load();
    List<WavModel> loadModels();
    List<Double> delLatentPeriod(WavModel wavModel);
    List<Double> normalize(WavModel wavModel);
    void showOnChart(LineChart currSignalChart,List<Double> bytes,String s);
    List<List<Double>> createBands(WavModel wavModel);
    List<Double> getLowPassFilterData(List<Double> band,int N, double fCP);
    List<Double> envelopExtracting(List<Double> wavBayts);
    void showSegmentationOnChart(LineChart currSignalChart,List<Integer> bytes,double max);
}
