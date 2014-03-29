package opt.test;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.*;
import opt.example.ContinuousPeaksEvaluationFunction;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.*;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.ConvergenceTrainer;
import shared.FixedIterationTrainer;
import shared.Instance;

import java.util.Arrays;
import java.util.Vector;

/**
 * 
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class FourPeaksTest {
    /** The n value */
    private static final int N = 60;
    /** The t value */
    private static final int T = N / 10;
    
    public static void main(String[] args) {
        int[] ranges = new int[N];
        Arrays.fill(ranges, 2);
        EvaluationFunction ef = new FourPeaksEvaluationFunction(T);
        Distribution odd = new DiscreteUniformDistribution(ranges);
        NeighborFunction nf = new DiscreteChangeOneNeighbor(ranges);
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        Distribution df = new DiscreteDependencyTree(.1, ranges); 
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);
        
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);      
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);
        fit.train();
        System.out.println(FourPeaksTest.formatInstance(rhc.getOptimal()));
        System.out.println(ef.value(rhc.getOptimal()));

        SimulatedAnnealing sa = new SimulatedAnnealing(1E11, .95, hcp);
        fit = new FixedIterationTrainer(sa, 200000);
        fit.train();
        System.out.println(FourPeaksTest.formatInstance(sa.getOptimal()));
        System.out.println(ef.value(sa.getOptimal()));
        
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        fit = new FixedIterationTrainer(ga, 1000);
        fit.train();
        System.out.println(FourPeaksTest.formatInstance(ga.getOptimal()));
        System.out.println(ef.value(ga.getOptimal()));
        
        MIMIC mimic = new MIMIC(200, 20, pop);
        fit = new FixedIterationTrainer(mimic, 1000);
        fit.train();
        System.out.println(FourPeaksTest.formatInstance(mimic.getOptimal()));
        System.out.println(ef.value(mimic.getOptimal()));

        mimic = new MIMIC(200, 20, pop);
        ConvergenceTrainer cfit = new ConvergenceTrainer(mimic);
        cfit.train();
        System.out.println(FourPeaksTest.formatInstance(mimic.getOptimal()));
        System.out.println(ef.value(mimic.getOptimal()));
        System.out.println(cfit.getIterations());

    }

    public static String formatInstance(Instance a) {
        util.linalg.Vector v = a.getData();
        Integer[] data = new Integer[v.size()];
        for (int i=0; i<v.size(); i++) {
            data[i] = ((Double) v.get(i)).intValue();
        }
        return Arrays.toString(data);
    }

}
