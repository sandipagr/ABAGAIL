package shared.test;

import shared.DataSet;
import shared.DataSetDescription;
import shared.DataSetWriter;
import shared.filt.LabelSplitFilter;
import shared.reader.ArffDataSetReader;
import shared.reader.DataSetReader;

import java.io.File;

/**
 * A data set reader
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class ArffIncomeDataSetReader {
    /**
     * The test main
     * @param args ignored parameters
     */
    public static void main(String[] args) throws Exception {
        DataSetReader dsr = new ArffDataSetReader(new File("").getAbsolutePath() + "/src/shared/test/adult.arff");

        // read in the raw data
        DataSet ds = dsr.read();

        // split out the label
        LabelSplitFilter lsf = new LabelSplitFilter();
        lsf.filter(ds);

        //ContinuousToDiscreteFilter ctdf = new ContinuousToDiscreteFilter(10);
        //ctdf.filter(ds);
        //DataSetLabelBinarySeperator.seperateLabels(ds);
        // System.out.println(ds);
        //System.out.println(new DataSetDescription(ds));


        DataSetWriter writer = new DataSetWriter(ds, new File("").getAbsolutePath() + "/src/shared/test/adult-normalized.csv");
        writer.write();
    }
}
