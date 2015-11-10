package cloud;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

public class CreateInitialPopulationMapReduce {
    public static class GenerateGeneMap extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            IntWritable itemKey;
            Random rand = new Random();

            word.set(tokenizer.nextToken());
            String weight = tokenizer.nextToken();
            for (int i = 0; i < Utils.POPULATION_SIZE; i++){
                itemKey = new IntWritable(i);
                if (rand.nextFloat() > 0.5) {
                    output.collect(itemKey, new Text(weight));
                } else {
                    output.collect(itemKey, new Text("0"));
                }
            }
        }
    }

    public static class GenerateGeneReduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        public void reduce (IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException{
            StringBuilder sb = new StringBuilder();

            while (values.hasNext()){
                String nextValue = values.next().toString();
                if ( !nextValue.isEmpty() && nextValue.length() > 0 && !"".equals(nextValue) ) {
                    sb.append(nextValue);
                    if ( values.hasNext() ){
                        sb.append(";");
                    }
                }
            }

            output.collect(key, new Text(sb.toString()));
        }
    }
}
