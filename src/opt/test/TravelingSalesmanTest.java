package opt.test;

import java.util.Arrays;
import java.util.Random;

import dist.DiscreteDependencyTree;
import dist.DiscretePermutationDistribution;
import dist.DiscreteUniformDistribution;
import dist.Distribution;

import opt.SwapNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.*;
import opt.ga.CrossoverFunction;
import opt.ga.SwapMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class TravelingSalesmanTest {
    /** The n value */
    private static final int N = 50;

    private double[][] points = null;

    public double[][] createRandomPoints(){

        if(points == null) {
            Random random = new Random();
            // create the random points
            points = new double[N][2];
            for (int i = 0; i < points.length; i++) {
                points[i][0] = random.nextDouble();
                points[i][1] = random.nextDouble();
            }
        }
        return points;
    }

    public void run(int iterations, int loop) {

        double[][] points = createRandomPoints();

        // for rhc, sa, and ga we use a permutation based encoding
        TravelingSalesmanEvaluationFunction ef = new TravelingSalesmanRouteEvaluationFunction(points);
        Distribution odd = new DiscretePermutationDistribution(N);
        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new TravelingSalesmanCrossOver(ef);
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

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
            SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .95, hcp);
            fit = new FixedIterationTrainer(sa, iterations);
            fit.train();
            optimalVal = ef.value(sa.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, SA", iterations, optimalVal, start, end));

            start = System.nanoTime();
            StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 150, 20, gap);
            fit = new FixedIterationTrainer(ga, iterations);
            fit.train();
            optimalVal = ef.value(ga.getOptimal());
            end = System.nanoTime();
            System.out.println(String.format("%d, %f, %d, %d, GA", iterations, optimalVal, start, end));

            // for mimic we use a sort encoding
            ef = new TravelingSalesmanSortEvaluationFunction(points);
            int[] ranges = new int[N];
            Arrays.fill(ranges, N);
            odd = new  DiscreteUniformDistribution(ranges);
            Distribution df = new DiscreteDependencyTree(.1, ranges);
            ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

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

        int[] iterations = {100, 500, 1000, 2000, 5000, 10000};
        TravelingSalesmanTest tsp = new TravelingSalesmanTest();
        for (int iter : iterations){
            tsp.run(iter, 5);
        }

    }

}
