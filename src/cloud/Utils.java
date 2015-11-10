package cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static final int MAXIMUM_WEIGHT = 500;
    public static final int POPULATION_SIZE = 50;

    public static final String INITIAL_POPULATION_FILE = "distributedKnapsack/files/initialPopulation.knp";
    public static final String POPULATION_PREFIX = "distributedKnapsack/files/population";
    public static final String SELECTED_PREFIX = "distributedKnapsack/files/selected";
    public static final String POPULATION_SUFFIX = ".knp";
    public static final int SURVIVES_KEY = 1;
    public static final int PERISHES_KEY = 2;

    public static Logger logger = LoggerFactory.getLogger(Utils.class);


    public static int calculateFitness(String gene){
        String[] chromosomes = gene.split(";");
        int sum = 0;
        int i = 0;
        while (chromosomes.length > 0 && i < chromosomes.length){
            String chromosome =chromosomes[i];

            if ( !"".equals(chromosome) ) {
                sum += Integer.parseInt(chromosome);
            }

            i++;
        }

        if ( sum <= Utils.MAXIMUM_WEIGHT ) {
            return sum;
        } else {
            return  0;
        }
    }

    public static List<String> shuffleArray(List<String> list) {
        String[] ar = new String[list.size()];
        ar = list.toArray(ar);

        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }

        list.clear();
        list.addAll(Arrays.asList(ar));

        return list;
    }
}
