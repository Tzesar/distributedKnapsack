package cloud;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class SelectFittestSolutionMapReduce {
    public static class SelectOptimumMap extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private Text word = new Text();
        private IntWritable fittestKey= new IntWritable(Utils.FITTEST_KEY);

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);

            word.set(tokenizer.nextToken());
            String gene = tokenizer.nextToken();

            output.collect( fittestKey, new Text(gene));
        }
    }

    public static class SelectOptimumReduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        private IntWritable fittestKey= new IntWritable(Utils.FITTEST_KEY);

        public void reduce (IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException{
            int biggestFitness = -1;
            Text fittestGene = new Text();

            while ( values.hasNext() ){
                Text gene = values.next();
                int fitness = Utils.calculateFitness( gene.toString() );

                if ( fitness > biggestFitness ){
                    biggestFitness = fitness;

                    fittestGene.set(gene);
                }
            }

            output.collect(new IntWritable(biggestFitness), fittestGene);
        }
    }
}
