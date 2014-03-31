package shared.test;

import shared.DataSet;
import shared.Instance;
import shared.filt.PrincipalComponentAnalysis;
import util.linalg.Matrix;

/**
 * A class for testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class PrincipalComponentAnalysisTest {
    

    public void run(String filename) {

        long start = System.nanoTime();
        DataSet set = FeatureSelectionUtil.getDataset(filename);

        System.out.println("Before PCA");
        PrincipalComponentAnalysis filter = new PrincipalComponentAnalysis(set, 2);

        System.out.println("Eigenvalues");
        System.out.println(filter.getEigenValues());

        System.out.println("Filter Matrix Projection Transpose");
        System.out.println(filter.getProjection().transpose());
        filter.filter(set);

        System.out.println("After PCA");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-pca.csv");

        long end = System.nanoTime();

        Matrix reverse = filter.getProjection().transpose();
        for (int i = 0; i < set.size(); i++) {
            Instance instance = set.get(i);
            instance.setData(reverse.times(instance.getData()).plus(filter.getMean()));
        }
        System.out.println("After reconstructing");
        FeatureSelectionUtil.writeFile(set, filename.split("\\.")[0] + "-pca-reconstruction.csv");
        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));

    }


    public static void main(String[] args) {
//        String filename = "/src/shared/test/data/wine/winequality.csv";
        String filename = "/src/shared/test/data/adult/adult-normalized.csv";

        long start = System.nanoTime();
        PrincipalComponentAnalysisTest ica = new PrincipalComponentAnalysisTest();
        ica.run(filename);
        long end = System.nanoTime();

        System.out.println(String.format("Took: %f seconds", (end - start)/Math.pow(10.0, 9.0)));
    }

}
