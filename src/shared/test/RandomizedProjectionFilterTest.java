package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.RandomizedProjectionFilter;
import util.linalg.Matrix;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class RandomizedProjectionFilterTest {
    

    public void run(String filename) {

        long start = System.nanoTime();
        DataSet set = FeatureSelectionUtil.getDataset(filename);

        System.out.println("Before RP");
        RandomizedProjectionFilter filter = new RandomizedProjectionFilter(2, set.get(0).size());

        System.out.println("Filter Matrix Projection");
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);

        System.out.println("After RP");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-rp2.csv");

        long end = System.nanoTime();

        System.out.println("After reconstructing");
        filter.reverse(set);
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-rp-reconstruction.csv");
        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }



    public static void main(String[] args) {
        String filename = "/src/shared/test/data/wine/winequality.csv";
//        String filename = "/src/shared/test/data/adult/adult-normalized.csv";

        long start = System.nanoTime();
        RandomizedProjectionFilterTest ica = new RandomizedProjectionFilterTest();
        ica.run(filename);
        long end = System.nanoTime();

        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }

}
