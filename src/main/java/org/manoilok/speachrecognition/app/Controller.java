package main.java.org.manoilok.speachrecognition.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.org.manoilok.speachrecognition.model.*;
import main.java.org.manoilok.speachrecognition.service.WavModelService;
import main.java.org.manoilok.speachrecognition.service.WavModelServiceImpl;

import java.util.*;


public class Controller {
    private Main main;
    private WavModel wavModel;
    private WavModelService wavModelService;
    private  boolean fourer;

    @FXML
    private LineChart currSignalChart;
    @FXML
    private LineChart withOutLPChart;
    @FXML
    private LineChart normalizeChart;
    @FXML
    private LineChart spectrChart;
    @FXML
    private ScrollBar scrollBar;
    @FXML
    private LineChart bandChart;
    @FXML
    private ScrollBar bandScrollBar;

    @FXML
    private TableView<RecgnitionResult> tableRecognition;

    @FXML
    private TableColumn<RecgnitionResult, String> fileNameColumn;

    @FXML
    private TableColumn<RecgnitionResult, Double> distanceColumn;
    @FXML
    TextField thresholdField;
    @FXML
    TextField nField;
    @FXML
    TextField fcpField;

    public void setMain(Main main) {
        this.main = main;
    }

    public void loadFile(){
        wavModelService=new WavModelServiceImpl();
        wavModel= new WavModel(wavModelService.load());
        currSignalChart.getData().clear();
        wavModelService.showOnChart(currSignalChart,wavModel.getWavBytes(),"");

        withOutLPChart.getData().clear();
        wavModel.setWavBytes(wavModelService.delLatentPeriod(wavModel));
        wavModelService.showOnChart(withOutLPChart,wavModel.getWavBytes(),"");

        normalizeChart.getData().clear();
        wavModel.setWavBytes(wavModelService.normalize(wavModel));
        wavModelService.showOnChart(normalizeChart,wavModel.getWavBytes(),"");
        wavModelService.showOnChart(normalizeChart,
                wavModelService.envelopExtracting(wavModel.getWavBytes()),"0");
        spectrChart.getData().clear();


    }
    public void loadModels() {
        ObservableList<RecgnitionResult> result =  FXCollections.observableArrayList();

        for (WavModel wm :wavModelService.loadModels() ){
            wm.setWavBytes(wavModelService.delLatentPeriod(wm));
            wm.setWavBytes(wavModelService.normalize(wm));
            if (fourer){
                FFT fft = new FFT();
                wm.setSpectr( fft.fff(wm.getWavBytes()));
            }
            else {
                Chebyshev chebyshev = new Chebyshev();
                wm.setSpectr( chebyshev.chebyshev(wm.getWavBytes()));
            }

            wm.setBand(wavModelService.createBands(wm));
            DTW dtw = new DTW(wavModel.getBand(),wm.getBand());
            result.add(new RecgnitionResult(wm.getFileName(),dtw.getDistance()));
        }
        tableRecognition.setItems(result);
    }

    @FXML
    private void initialize() {
        distanceColumn.setCellValueFactory(new PropertyValueFactory<RecgnitionResult, Double>("distance"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<RecgnitionResult, String>("filename"));
    }

    public void moveScrollBar(){
        int value = (int) scrollBar.getValue();
        spectrChart.getData().clear();
        wavModelService.showOnChart(spectrChart,
                wavModel.getSpectr().get(value),Integer.toString(value));
        System.out.print(scrollBar.getValue());

    }

    public void moveBandScrollBar(){
        int value = (int) bandScrollBar.getValue();
        bandChart.getData().clear();
        wavModelService.showOnChart(bandChart,
                wavModel.getBand().get(value),Integer.toString(value));
        int N = 0;
        double fcp=0;
        try {
            fcp=Double.parseDouble(fcpField.getText());
            N= Integer.parseInt(nField.getText());
        }
        catch ( Exception e){
            System.out.print("Wrong input for fcp or N!");
        }

        wavModelService.showOnChart(bandChart,
                wavModelService.getLowPassFilterData( wavModel.getBand().get(value),N,fcp),"0");

        System.out.print(bandScrollBar.getValue());

    }

    public void fourerButton(){
        fourer = true;

        FFT fft = new FFT();
        wavModel.setSpectr( fft.fff(wavModel.getWavBytes()));
        visualizeDecomposition();

    }

    public void chebyshevButton(){
        fourer = false;
        Chebyshev chebyshev = new Chebyshev();
        wavModel.setSpectr( chebyshev.chebyshev(wavModel.getWavBytes()));
        visualizeDecomposition();
    }

    private void visualizeDecomposition(){
        spectrChart.getData().clear();
        wavModelService.showOnChart(spectrChart,wavModel.getSpectr().get(0),"0");

        scrollBar.setMax(wavModel.getSpectr().size()-1);
        bandChart.getData().clear();
        wavModel.setBand(wavModelService.createBands(wavModel));
        wavModelService.showOnChart(bandChart,wavModel.getBand().get(0),"0");
        bandScrollBar.setMax(wavModel.getBand().size()-1);
    }

    public void applySegmentationButton(){
        double threshold=0;
        try {
             threshold=Double.parseDouble(thresholdField.getText());
        }
        catch ( Exception e){
            System.out.print("Wrong input for threshold!");
        }
        ActivityDetector activityDetector = new ActivityDetector(threshold,256,256);
        List<Integer> segmentation = activityDetector.process(wavModelService.envelopExtracting(wavModel.getWavBytes()));

        wavModelService.showSegmentationOnChart(normalizeChart,
                segmentation,Collections.max( wavModel.getWavBytes()));
    }

    public void applyLPFilterButton(){
        int N = 0;
        double fcp=0;
        try {
            fcp=Double.parseDouble(fcpField.getText());
            N= Integer.parseInt(nField.getText());
        }
        catch ( Exception e){
            System.out.print("Wrong input for fcp or N!");
        }

        wavModelService.showOnChart(bandChart,
                wavModelService.getLowPassFilterData( wavModel.getBand().get(0),N,fcp),"0");
    }
}
