package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.DiscreteChangeOneNeighbor;
import opt.EvaluationFunction;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.UniformCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * A test of the knap sack problem
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class KnapsackTest {
    /** Random number generator */
    private static final Random random = new Random();
    /** The number of items */
    private static final int NUM_ITEMS = 40;
    /** The number of copies each */
    private static final int COPIES_EACH = 4;
    /** The maximum weight for a single element */
    private static final double MAX_WEIGHT = 50;
    /** The maximum volume for a single element */
    private static final double MAX_VOLUME = 50;
    /** The volume of the knapsack */
    private static final double KNAPSACK_VOLUME = 
         MAX_VOLUME * NUM_ITEMS * COPIES_EACH * .4;

    public int[] copies, ranges;
    public double[] weights, volumes;

    public KnapsackTest(){
        createRandomPoints();
    }

    public void createRandomPoints(){
        copies = new int[NUM_ITEMS];
        Arrays.fill(copies, COPIES_EACH);
        weights = new double[NUM_ITEMS];
        volumes = new double[NUM_ITEMS];
        for (int i = 0; i < NUM_ITEMS; i++) {
            weights[i] = random.nextDouble() * MAX_WEIGHT;
            volumes[i] = random.nextDouble() * MAX_VOLUME;
        }
        ranges = new int[NUM_ITEMS];
        Arrays.fill(ranges, COPIES_EACH + 1);
    }

    public void run(int iterations, int loop) {

        EvaluationFunction ef = new KnapsackEvaluationFunction(weights, volumes, KNAPSACK_VOLUME, copies);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new UniformCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        long start, end;
        double optimalVal;

        for (int i = 0; i < loop; i++) {
            start = System.nanoTime();
            RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
            FixedIterationTrainer fit = new FixedIterationTrainer(rhc, iterations);
            fit.train();
            optimalVal = ef.value(rhc.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, RHC", iterations, optimalVal, start, end));

            start = System.nanoTime();
            SimulatedAnnealing sa = new SimulatedAnnealing(100, .95, hcp);
            fit = new FixedIterationTrainer(sa, iterations);
            fit.train();
            optimalVal = ef.value(sa.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, SA", iterations, optimalVal, start, end));

            start = System.nanoTime();
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 25, gap);
            fit = new FixedIterationTrainer(ga, iterations);
            fit.train();
            optimalVal = ef.value(ga.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, GA", iterations, optimalVal, start, end));


            start = System.nanoTime();
            MIMIC mimic = new MIMIC(200, 100, pop);
            fit = new FixedIterationTrainer(mimic, iterations);
            fit.train();
            optimalVal = ef.value(mimic.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, MIMIC", iterations, optimalVal, start, end));
        }
    }

    public static void main(String[] args) {
        int[] iterations = {100, 200, 500, 1000, 2000, 5000, 10000};
        KnapsackTest kp = new KnapsackTest();
        for (int iter : iterations){
            kp.run(iter, 5);
        }
    }

}
