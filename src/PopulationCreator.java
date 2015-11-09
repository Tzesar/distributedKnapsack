import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by Augusto on 09/11/2015.
 */
public class PopulationCreator {
    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable>{
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException{
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);

            while (tokenizer.hasMoreTokens()){
                word.set(tokenizer.nextToken());
                output.collect(word, one);
            }
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable>{
        public void reduce (Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException{
            int sum = 0;

            while (values.hasNext()){
                sum += values.next().get();
            }

            output.collect(key, new IntWritable(sum));
        }
    }

    public static void main(String [] args) throws Exception {
        JobConf conf = new JobConf(PopulationCreator.class);
        conf.setJobName("populationCreator");
        FileSystem fs = FileSystem.get(conf);

//        Path inputTest = new Path("distributedKnapsack/files/inTest");
        Path outputTest = new Path("distributedKnapsack/files/outTest");

//        if (!fs.exists(inputTest))
//            printAndExit("Input file not found");
//        if (!fs.isFile(inputTest))
//            printAndExit("Input should be a file");
//        if (fs.exists(outputTest))
//            printAndExit("Output already exists");

        int maxPopulation = 100;
        Integer i = 0;
        try {
            BufferedWriter out = new BufferedWriter( new OutputStreamWriter(fs.create(outputTest, true)));
            while (i < maxPopulation) {
                out.write(i.toString());
                out.write(" ");
                out.write("0");
                out.write("\n");

                i++;
            }
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR [main] - While initializing population");
        }


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

    static void printAndExit(String str) {
        System.err.println(str);
        System.exit(1);
    }
}
