package opt.test;

import java.util.*;


public class WineQualityLoop {

    public static void main(String[] args) {

        // int[] training = {100, 200, 500, 1000, 2000, 5000, 10000};
        int[] training = {500};
        int repitition = 5;
        ArrayList<String> results = new ArrayList<String>();

        for (int i = 0; i < training.length; i++) {
            for (int j = 0; j < repitition; j++) {
                System.out.println(j + "th iteration for training=" + training[i]);
                WineQualityTest wq = new WineQualityTest(training[i]);
                ArrayList stats = wq.run();

                String strStat = training[i] + ", " + stats.toString().replace("[", "").replace("]", "");
                System.out.println(strStat);
                results.add(strStat);
            }

            System.out.println(results.toString());
        }

        System.out.println("Final Results =================");
        for (int i = 0; i < results.size(); i++) {
            System.out.println(results.get(i));
        }

    }
}
