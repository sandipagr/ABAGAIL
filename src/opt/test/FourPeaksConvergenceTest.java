package opt.test;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.*;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.*;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.*;

import java.util.Arrays;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FourPeaksConvergenceTest {
    /** The n value */
    private int N = 60;
    /** The t value */
    private int T = N / 10;

    private EvaluationFunction ef;

    public FourPeaksConvergenceTest(int N){
        this.N = N;
        this.T = this.N / 10;
    }
    
    public void run() {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        int numIterations;
        double optimalVal;
        long startTime, endTime;


        startTime = System.nanoTime();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        numIterations = train(rhc);
        optimalVal = ef.value(rhc.getOptimal());
        endTime = System.nanoTime();
        System.out.println(String.format("%d, %d, %f, %d, %d, RHC", N, numIterations, optimalVal, startTime, endTime));

        if(startTime > 1){
            return;
        }

        startTime = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
        numIterations = train(sa);
        optimalVal = ef.value(sa.getOptimal());
        endTime = System.nanoTime();
        System.out.println(String.format("%d, %d, %f, %d, %d, SA", N, numIterations, optimalVal, startTime, endTime));

        startTime = System.nanoTime();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        numIterations = train(ga);
        optimalVal = ef.value(ga.getOptimal());
        endTime = System.nanoTime();
        System.out.println(String.format("%d, %d, %f, %d, %d, GA", N, numIterations, optimalVal, startTime, endTime));

        startTime = System.nanoTime();
        MIMIC mimic = new MIMIC(200, 20, pop);
        numIterations = train(mimic);
        optimalVal = ef.value(mimic.getOptimal());
        endTime = System.nanoTime();
        System.out.println(String.format("%d, %d, %f, %d, %d, MIMIC", N, numIterations, optimalVal, startTime, endTime));

    }

    public static String formatInstance(Instance a) {
        util.linalg.Vector v = a.getData();
        Integer[] data = new Integer[v.size()];
        for (int i=0; i<v.size(); i++) {
            data[i] = ((Double) v.get(i)).intValue();
        }
        return Arrays.toString(data);
    }

    public int train(OptimizationAlgorithm trainer){
        int optVal = N + (N - (T + 1));
        int iterations = 0;
        int maxIterations = 2000000;

        double iterValue = 0;

        while (iterations < maxIterations){
            iterations++;
            trainer.train();
            iterValue = ef.value(trainer.getOptimal());

            if (Math.ceil(iterValue) == optVal){
                break;
            }
        }

        return iterations;
    }


    public static void main(String[] args) {

        int[] NUMS = {80};

        for (int num: NUMS){
            for (int i = 0; i < 25 ; i++) {
                FourPeaksConvergenceTest fp = new FourPeaksConvergenceTest(num);
                fp.run();
            }
        }
    }
}
