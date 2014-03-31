package shared.test;

import shared.DataSet;
import shared.DataSetWriter;
import shared.filt.LabelSplitFilter;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;

import java.io.File;
import java.io.IOException;

/**
 * Created by sandipagrawal on 3/29/14.
 */
public class FeatureSelectionUtil {


    public static DataSet getDataset(String filename){
        DataSetReader dsr = new CSVDataSetReader(new File("").getAbsolutePath() + filename);
        DataSet ds = null;
        try {
            ds = dsr.read();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // split out the label
        LabelSplitFilter lsf = new LabelSplitFilter();
        lsf.filter(ds);

        return ds;
    }


    public static void writeFile(DataSet ds, String outFile) {
        DataSetWriter writer = new DataSetWriter(ds, new File("").getAbsolutePath() + outFile);
        try {
            writer.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
