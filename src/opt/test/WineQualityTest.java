package opt.test;

import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Implementation of randomized hill climbing, simulated annealing, and genetic algorithm to
 * find optimal weights to a neural network that is classifying abalone as having either fewer 
 * or more than 15 rings. 
 *
 * @author Hannah Lau
 * @version 1.0
 */
public class WineQualityTest {
    public Instance[] instances = initializeInstances();

    public int inputLayer = 11, hiddenLayer = 9, outputLayer = 1, trainingIterations = 1000;
    public BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

    public ErrorMeasure measure = new SumOfSquaresError();

    public DataSet set = new DataSet(instances);
    public BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    public NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];

    public OptimizationAlgorithm[] oa = new OptimizationAlgorithm[3];
    public String[] oaNames = {"RHC", "SA", "GA"};
    public String results = "";

    public DecimalFormat df = new DecimalFormat("0.000");

    public WineQualityTest(int trainingIterations){
        this.trainingIterations = trainingIterations;
    }

    public ArrayList run() {
        ArrayList stats = new ArrayList();

        for(int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                new int[] {inputLayer, hiddenLayer, outputLayer});
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
        }

        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E11, .95, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);

        for(int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i]); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10,9);

            Instance optimalInstance = oa[i].getOptimal();
            // System.out.println(optimalInstance.size() + ":" + optimalInstance.getLabel() + ":" + optimalInstance.getWeight() + ":" + optimalInstance.getData());
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            start = System.nanoTime();
            for(int j = 0; j < instances.length; j++) {
                networks[i].setInputValues(instances[j].getData());
                networks[i].run();

                predicted = Double.parseDouble(instances[j].getLabel().toString());
                actual = Double.parseDouble(networks[i].getOutputValues().toString());

                // System.out.println("Predicted:" + predicted + " actual:"+ actual);

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10,9);

            results +=  "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                        "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                        + df.format(correct/(correct+incorrect)*100) + "%\nTraining time: " + df.format(trainingTime)
                        + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n";

            stats.addAll(Arrays.asList(correct, incorrect, trainingTime, testingTime));

        }

        System.out.println(results);
        System.out.println(stats);
        return stats;
    }

    private void train(OptimizationAlgorithm oa, BackPropagationNetwork network, String oaName) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");

        for(int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for(int j = 0; j < instances.length; j++) {
                network.setInputValues(instances[j].getData());
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);
            }

            // System.out.println(df.format(error));
        }
    }

    private Instance[] initializeInstances() {

        double[][][] attributes = new double[1599][][]; // Number of data points
        int num_attributes = 11; // Excluding label

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File("src/opt/test/winequality.txt")));

            for(int i = 0; i < attributes.length; i++) {
                Scanner scan = new Scanner(br.readLine());
                scan.useDelimiter(",");

                attributes[i] = new double[2][];
                attributes[i][0] = new double[num_attributes];
                attributes[i][1] = new double[1];

                for(int j = 0; j < num_attributes; j++)
                    attributes[i][0][j] = Double.parseDouble(scan.next());

                attributes[i][1][0] = Double.parseDouble(scan.next());
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Instance[] instances = new Instance[attributes.length];

        for(int i = 0; i < instances.length; i++) {
            instances[i] = new Instance(attributes[i][0]);
            // classifications range from 3 to 8; split into 3 - 5 and 6 - 8
            instances[i].setLabel(new Instance(attributes[i][1][0] < 6 ? 0 : 1));
        }

        return instances;
    }

    public static void main(String[] args){
        WineQualityTest wq = new WineQualityTest(100);
        wq.run();
    }

}
