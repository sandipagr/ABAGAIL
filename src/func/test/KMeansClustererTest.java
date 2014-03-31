package func.test;

import dist.Distribution;
import dist.MultivariateGaussian;
import func.KMeansClusterer;
import shared.DataSet;
import shared.Instance;
import shared.test.FeatureSelectionUtil;
import util.linalg.DenseVector;
import util.linalg.RectangularMatrix;

/**
 * Testing
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KMeansClustererTest {
    /**
     * The test main
     * @param args ignored
     */
    public static void main(String[] args) throws Exception {
        String filename = "";
        filename = "/src/shared/test/data/wine/winequality-%s.csv";
        filename = "/src/shared/test/data/adult/adult-normalized-%s.csv";

        String[] algos = {"ica"}; //, "isca", "pca", "rp"};
        for (int i = 0; i < algos.length; i++) {
            String algo = algos[i];

            String file = String.format(filename, algo);

            System.out.println(file);
            long start = System.nanoTime();

            DataSet set = FeatureSelectionUtil.getDataset(file);
            KMeansClusterer km = new KMeansClusterer(2);
            km.estimate(set);
            long end = System.nanoTime();

            System.out.println(km.mode());

            //System.out.println(km);
            //System.out.println(String.format("%f seconds", (end - start)/Math.pow(10.0, 9.0)));
        }

    }
}
