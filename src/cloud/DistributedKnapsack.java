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

public class DistributedKnapsack {

    public static void main(String [] args) throws Exception {
        int populationCount = 1;
        JobConf conf = new JobConf(DistributedKnapsack.class);
        FileSystem fs = FileSystem.get(conf);
        conf.setJobName("populationCreator");

        createInitialPopulation(fs);

        conf.setOutputKeyClass(IntWritable.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(GenerateGeneMap.class);
        conf.setCombinerClass(GenerateGeneReduce.class);
        conf.setReducerClass(GenerateGeneReduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path("distributedKnapsack/files/initialPopulation.knp"));
        FileOutputFormat.setOutputPath(conf, new Path("distributedKnapsack/files/population-"+ populationCount +".knp"));

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
                out.write(""+rand.nextInt(Utils.maximumWeight));
                out.write("\n");

                i++;
            }
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR [main] - While initializing population");
        }
    }
}
