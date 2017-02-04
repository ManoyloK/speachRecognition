package main.java.org.manoilok.speachrecognition.service;

import main.java.org.manoilok.speachrecognition.model.LowPassFilter;
import main.java.org.manoilok.speachrecognition.model.OpenFileDialog;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import main.java.org.manoilok.speachrecognition.model.WavModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Modest on 09.09.2016.
 */
public class WavModelServiceImpl implements WavModelService{
    private final int  USELESS_BYTE_COUNT = 43;
    private final int[] BAND_SPECTRAL_DECOMPOSITION= {0,2,4,6,8,10,15,25,50,128};
    private final int[] BAND_SPECTRAL_DECOMPOSITION_CHEBYSHEV= {0,4,8,12,16,20,30,50,100,256};

    @Override
    public List<Double> load() {
        OpenFileDialog dialog = new OpenFileDialog();
        String path = dialog.getFile().getPath();
        List<Double> wavBytes= new ArrayList<>();

        try (RandomAccessFile data = new RandomAccessFile(new File(path), "r")) {
            byte[] eight ;
            data.readFully(eight=new byte[USELESS_BYTE_COUNT]);
            for (long i = 0, len = (data.length()-USELESS_BYTE_COUNT) / 2; i < len; i++) {
                data.readFully(eight = new byte[2]);
                wavBytes.add(new BigInteger(eight).doubleValue());
            }
            data.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wavBytes;

    }

    @Override
    public  List<WavModel> loadModels() {
        OpenFileDialog dialog = new OpenFileDialog();
        List<List<Double>> wavBytesEtalons= new ArrayList<>();
        List<WavModel> etalons = new ArrayList<>();
        int etalonSize;
        for (File f : dialog.getAllFiles()) {
            wavBytesEtalons.add(new ArrayList<>());
            etalonSize = wavBytesEtalons.size();
            try (RandomAccessFile data = new RandomAccessFile(new File(f.getPath()), "r")) {
                byte[] eight;
                data.readFully(eight = new byte[USELESS_BYTE_COUNT]);
                for (long i = 0, len = (data.length() - USELESS_BYTE_COUNT) / 2; i < len; i++) {
                    data.readFully(eight = new byte[2]);
                    wavBytesEtalons.get(etalonSize-1).add(new BigInteger(eight).doubleValue());
                }
                data.close();
            }  catch (IOException e) {
                e.printStackTrace();
            }
            etalons.add(new WavModel(wavBytesEtalons.get(etalonSize-1),f.getName()));
        }
        return etalons;

    }

    @Override
    public List<Double> delLatentPeriod( WavModel wavModel ) {
        double threshold= MathHelper.threshold( wavModel.getWavBytes() );
        List<Double> bytes=wavModel.getWavBytes();
        boolean cutOnBegin=true,
                cutOnEnd = true;
        while (cutOnBegin||cutOnEnd){

            if (Math.abs(bytes.get(0))<threshold){
                bytes.remove(0);
            }
            else{
                cutOnBegin=false;
                if (bytes.get(bytes.size()-1) < threshold){
                    bytes.remove(bytes.size()-1);
                }
                else{
                    cutOnEnd = false;
                    break;

                }
            }
        }

        return bytes;

    }

    @Override
    public List<Double> normalize(WavModel wavModel) {
        List<Double> bytes = wavModel.getWavBytes();
        double disp = MathHelper.dispersion(bytes);
        for (int i=0; i< bytes.size();i++){
            double value=bytes.get(i);
            bytes.set(i, (value/(disp)));
        }
        return bytes;
    }

    @Override
    public void showOnChart(LineChart currSignalChart,List<Double> bytes,String s) {
        currSignalChart.setTitle("");

        XYChart.Series series = new XYChart.Series();
        series.setName(s);
        int loopCycle=0;
        for (double y:bytes) {
            series.getData().add(new XYChart.Data(loopCycle++, y));
        }
        currSignalChart.setCreateSymbols(false);
        currSignalChart.setLegendVisible(false);
        currSignalChart.getData().addAll(series);
    }
    @Override
    public void showSegmentationOnChart(LineChart currSignalChart,List<Integer> bytes,double max) {
        currSignalChart.setTitle("");

        XYChart.Series series = new XYChart.Series();
        series.setName("0");
        for (double y:bytes) {
            series.getData().add(new XYChart.Data(y, 0));
            series.getData().add(new XYChart.Data(y, max));
            series.getData().add(new XYChart.Data(y, 0));
        }
        currSignalChart.setCreateSymbols(false);
        if (currSignalChart.getData().size() > 2) {
            currSignalChart.getData().remove(currSignalChart.getData().size()-1);
        }

        currSignalChart.getData().addAll(series);
    }
    @Override
    public List<List<Double>> createBands(WavModel wavModel)
    {
        List<List<Double>> lineSpectr= new ArrayList<>();
        int rowsCount= wavModel.getSpectr().size();
        int[] bandDecomposition;
        for ( int band=0; band<BAND_SPECTRAL_DECOMPOSITION.length-1;band++) {
            lineSpectr.add(new ArrayList<>());
            for (int i = 0; i < rowsCount; i++) {

                double value = 0;
                bandDecomposition= BAND_SPECTRAL_DECOMPOSITION;
                if (wavModel.getSpectr().get(0).size()>128){
                    bandDecomposition = BAND_SPECTRAL_DECOMPOSITION_CHEBYSHEV;
                }
                for (int j = bandDecomposition[band]; j < bandDecomposition[band + 1]; j++) {
                    value += wavModel.getSpectr().get(i).get(j);
                }
                lineSpectr.get(lineSpectr.size() - 1).add(value);
            }
        }
        return lineSpectr;
    }


    @Override
    public List<Double> getLowPassFilterData(List<Double> band,int N, double fCP) {
        LowPassFilter lpf= new LowPassFilter(N,fCP);

        return lpf.applyLPfilter(band);
    }

    @Override
    public List<Double> envelopExtracting(List<Double> wavBayts) {
        LowPassFilter lpf= new LowPassFilter(0,0);
        List<Double>absoluteWavBytes = wavBayts.stream().map(Math::abs).collect(Collectors.toList());
        return lpf.applyLPfilter2(absoluteWavBytes);

    }
}
