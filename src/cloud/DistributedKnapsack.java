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
        JobConf conf = new JobConf(DistributedKnapsack.class);
        conf.setJobName("populationCreator");
        FileSystem fs = FileSystem.get(conf);

        createInitialPopulation(fs);

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);

        conf.setMapperClass(Map.class);
        conf.setCombinerClass(Reduce.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path("distributedKnapsack/files/input"));
        FileOutputFormat.setOutputPath(conf, new Path("distributedKnapsack/files/output"));

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
