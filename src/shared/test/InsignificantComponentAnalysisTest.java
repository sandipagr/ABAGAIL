package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.InsignificantComponentAnalysis;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class InsignificantComponentAnalysisTest {
    

    public void run(String filename) {

        long start = System.nanoTime();
        DataSet set = FeatureSelectionUtil.getDataset(filename);


        System.out.println("Before IsCA");
        InsignificantComponentAnalysis filter = new  InsignificantComponentAnalysis(set, 2);

        System.out.println("Eigenvalues");
        System.out.println(filter.getEigenValues());

        System.out.println("Filter Matrix Projection Transpose");
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);

        System.out.println("After IsCA");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-isca.csv");

        long end = System.nanoTime();

        Matrix reverse = filter.getProjection().transpose();
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(reverse.times(instance.getData()).plus(filter.getMean()));
        }
        System.out.println("After reconstructing");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-isca-reconstruction.csv");
        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }



    public static void main(String[] args) {
//        String filename = "/src/shared/test/data/wine/winequality.csv";
        String filename = "/src/shared/test/data/adult/adult-normalized.csv";

        long start = System.nanoTime();
        InsignificantComponentAnalysisTest ica = new InsignificantComponentAnalysisTest();
        ica.run(filename);
        long end = System.nanoTime();

        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }

}
