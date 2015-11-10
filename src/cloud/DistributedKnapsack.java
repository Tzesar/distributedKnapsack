package cloud;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import cloud.CreateInitialPopulationMapReduce.*;
import cloud.SelectGeneMapReduce.*;

public class DistributedKnapsack {

    public static void main(String [] args) throws Exception {
        int generationCount = 1;

        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        createInitialPopulation(fs);

        makePopulation(generationCount);

        selectGenes(generationCount);
    }

    private static void selectGenes(int generationCount) throws Exception{
        JobConf conf = new JobConf(DistributedKnapsack.class);
        conf.setJobName("geneSelector-["+generationCount+"]");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(SelectGeneMap.class);
        conf.setCombinerClass(SelectGeneReduce.class);
        conf.setReducerClass(SelectGeneReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(Utils.POPULATION_PREFIX +"-"+ (generationCount-1) + Utils.POPULATION_SUFFIX));
        FileOutputFormat.setOutputPath(conf, new Path(Utils.POPULATION_PREFIX +"-"+ generationCount + Utils.POPULATION_SUFFIX));

        JobClient.runJob(conf);
    }

    private static void makePopulation(int generationCount) throws Exception{
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        conf.setJobName("populationCreator-gen["+generationCount+"]");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(GenerateGeneMap.class);
        conf.setCombinerClass(GenerateGeneReduce.class);
        conf.setReducerClass(GenerateGeneReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(Utils.INITIAL_POPULATION_FILE));
        FileOutputFormat.setOutputPath(conf, new Path(Utils.POPULATION_PREFIX +"-"+ generationCount + Utils.POPULATION_SUFFIX));

        JobClient.runJob(conf);
    }

    private static void createInitialPopulation(FileSystem fs) {
        Path outputTest = new Path("distributedKnapsack/files/initialPopulation.knp");

        int maxPopulation = 100;
        Random rand = new Random();
        Integer i = 0;
        try {
            BufferedWriter out = new BufferedWriter( new OutputStreamWriter(fs.create(outputTest, true)));
            while (i < maxPopulation) {
                out.write(i.toString());
                out.write(" ");
                out.write(""+rand.nextInt(Utils.MAXIMUM_WEIGHT));
                out.write("\n");

                i++;
            }
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR [main] - While initializing population");
        }
    }
}
