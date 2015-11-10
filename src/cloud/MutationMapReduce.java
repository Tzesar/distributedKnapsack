package cloud;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

public class MutationMapReduce {
    public static class MutationMap extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private Text word = new Text();

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            int itemKey;
            Random rand = new Random();

            itemKey = (rand.nextFloat() <= Utils.MUTATION_CHANCE) ? Utils.GONNA_MUTATE : Utils.REMAIN_UNCHANGED;


            word.set(tokenizer.nextToken());
            String gene = tokenizer.nextToken();

            output.collect( new IntWritable(itemKey), new Text(gene));
        }
    }

    public static class MutationReduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        private int itemKey;
        public void reduce (IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException{

            Random rand = new Random();
            StringBuilder mutatedGene = new StringBuilder();


            while(values.hasNext()) {
                String currentGene = values.next().toString();

                if (key.get() == Utils.GONNA_MUTATE) {

                    int chromosomeToMutate = rand.nextInt(Utils.ITEM_COUNT);
                    String splitGene[] = currentGene.split(";");

                    if (splitGene[chromosomeToMutate].contains("-"))
                        splitGene[chromosomeToMutate].replace("-","");
                    else
                        splitGene[chromosomeToMutate] = "-" + splitGene[chromosomeToMutate];

                    for (int i = 1; i < Utils.ITEM_COUNT; i++){
                        mutatedGene.append(splitGene[i]);

                        if (i != Utils.ITEM_COUNT -1)
                            mutatedGene.append(";");
                    }

                    output.collect(new IntWritable(itemKey++), new Text(mutatedGene.toString()));
                }
                else {
                    output.collect(new IntWritable(itemKey++), new Text(currentGene));
                }

            }
        }
    }
}
