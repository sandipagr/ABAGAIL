package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.IndependentComponentAnalysis;
import util.linalg.Matrix;
import util.linalg.RectangularMatrix;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class IndepenentComponentAnalysisTest {

    public String filename;

    public IndepenentComponentAnalysisTest(String filename){
        this.filename = filename;

    }

    public void run() {
        DataSet set = FeatureSelectionUtil.getDataset(filename);


        System.out.println("Before randomizing");
        Matrix projection = new RectangularMatrix(new double[][]{ {.6, .6}, {.4, .6}});
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(projection.times(instance.getData()));
        }

        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-pre-ica-2c.csv");

        System.out.println("PRE ICA");
        IndependentComponentAnalysis filter = new IndependentComponentAnalysis(set, 2);
        filter.filter(set);
        System.out.println("After ICA");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-ica-2c.csv");
          
    }

    public static void main(String[] args) {
//        String filename = "/src/shared/test/data/wine/winequality.csv";
        String filename = "/src/shared/test/data/adult/adult-normalized.csv";

        long start = System.nanoTime();
        IndepenentComponentAnalysisTest ica = new IndepenentComponentAnalysisTest(filename);
        ica.run();
        long end = System.nanoTime();

        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }

}