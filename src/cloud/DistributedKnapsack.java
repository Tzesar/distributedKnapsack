package cloud;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import cloud.GenerateGeneMapReduce.*;
import cloud.SelectGeneMapReduce.*;
import cloud.CrossOverMapReduce.*;
import cloud.SelectFittestSolutionMapReduce.*;

public class DistributedKnapsack {

    public static void main(String [] args) throws Exception {
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        createInitialPopulation(fs);

        makePopulation(1);
        int generationCount = 1;
        for( generationCount = 1; generationCount <= Utils.MAXIMUM_GENERATION; generationCount++ ) {
            selectGenes(generationCount);
            crossoverGenes(generationCount);
        }

        selectFittestSolution(Utils.POPULATION_PREFIX+ "-" + generationCount + Utils.FILES_SUFFIX);
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

        Path outputPath = new Path(Utils.POPULATION_PREFIX +"-"+ generationCount);
        FileInputFormat.setInputPaths(conf, new Path(Utils.INITIAL_POPULATION_FILE));
        FileOutputFormat.setOutputPath(conf, outputPath);

        JobClient.runJob(conf);

        Path mergedOutputPath = new Path(Utils.POPULATION_PREFIX +"-"+ generationCount + Utils.FILES_SUFFIX);
        FileUtil.copyMerge(fs, outputPath, fs, mergedOutputPath, true, conf, "");
    }

    private static void selectGenes(int generationCount) throws Exception{
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        conf.setJobName("geneSelector-["+generationCount+"]");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(SelectGeneMap.class);
        conf.setReducerClass(SelectGeneReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        Path outputPath = new Path(Utils.SELECTED_PREFIX +"-"+ generationCount);
        FileInputFormat.setInputPaths(conf, new Path(Utils.POPULATION_PREFIX +"-"+ generationCount + Utils.FILES_SUFFIX));
        FileOutputFormat.setOutputPath(conf, outputPath);

        JobClient.runJob(conf);

        Path mergedOutputPath = new Path(Utils.SELECTED_PREFIX +"-"+ generationCount + Utils.FILES_SUFFIX);
        FileUtil.copyMerge(fs, outputPath, fs, mergedOutputPath, true, conf, "");
    }

    private static void crossoverGenes(int generationCount) throws Exception{
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        conf.setJobName("geneCrossover-["+generationCount+"]");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(CrossOverMap.class);
        conf.setReducerClass(CrossOverReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        Path outputPath = new Path(Utils.CROSSOVER_PREFIX +"-"+ generationCount);
        FileInputFormat.setInputPaths(conf, new Path(Utils.SELECTED_PREFIX +"-"+ generationCount + Utils.FILES_SUFFIX));
        FileOutputFormat.setOutputPath(conf, outputPath);

        JobClient.runJob(conf);

        Path mergedOutputPath = new Path(Utils.CROSSOVER_PREFIX +"-"+ generationCount + Utils.FILES_SUFFIX);
        FileUtil.copyMerge(fs, outputPath, fs, mergedOutputPath, true, conf, "");

        FileUtil.copy(fs, mergedOutputPath, fs, new Path(Utils.POPULATION_PREFIX+"-"+ (generationCount + 1) + Utils.FILES_SUFFIX), false, false, conf);
    }

    private static void createInitialPopulation(FileSystem fs) {
        Path outputFile = new Path(Utils.INITIAL_POPULATION_FILE);

        int maxPopulation = Utils.ITEM_COUNT;
        Random rand = new Random();
        Integer i = 0;
        try {
            BufferedWriter out = new BufferedWriter( new OutputStreamWriter(fs.create(outputFile, true)));
            while (i < maxPopulation) {
                out.write(i.toString());
                out.write(" ");
                out.write(""+rand.nextInt(15));
                out.write("\n");

                i++;
            }
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR [main] - While initializing population");
        }
    }

    private static void selectFittestSolution(String finalPopulationPathString) throws Exception{
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);

        conf.setJobName("selectFittestSolution-from-["+ finalPopulationPathString +"]");

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(SelectOptimumMap.class);
        conf.setReducerClass(SelectOptimumReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        Path outputPath = new Path(Utils.OPTIMUM_SOLUTION_FILE);
        FileInputFormat.setInputPaths(conf, new Path(finalPopulationPathString));
        FileOutputFormat.setOutputPath(conf, outputPath);

        JobClient.runJob(conf);

        Path mergedOutputPath = new Path(Utils.OPTIMUM_SOLUTION_FILE + Utils.FILES_SUFFIX);
        FileUtil.copyMerge(fs, outputPath, fs, mergedOutputPath, true, conf, "");
    }
}
