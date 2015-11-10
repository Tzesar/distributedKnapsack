package cloud;

public class Utils {
    public static final int MAXIMUM_WEIGHT = 500;
    public static final int POPULATION_SIZE = 20;
    public static final int ITEM_COUNT = 40;
    public static final int TOTAL_CAPACITY = 800;

    public static final double CROSSOVER_PROBABILITY = 0.5;
    public static final double MUTATION_CHANCE = 0.04;

    public static final String INITIAL_POPULATION_FILE = "distributedKnapsack/files/input/initialPopulation.knp";
    public static final String POPULATION_PREFIX = "distributedKnapsack/files/population";
    public static final String POPULATION_SUFFIX = ".knp";

    public static final String TEST_INITIAL_POPULATION_FILE = "testCrossOver/files/input/initialPopulation.knp";
    public static final String TEST_POPULATION_PREFIX = "testCrossOver/files/population";
    public static final String TEST_POPULATION_SUFFIX = ".knp";
}
