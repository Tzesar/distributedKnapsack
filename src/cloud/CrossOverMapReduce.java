package cloud;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;

public class CrossOverMapReduce {
    public static class CrossOverMap extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        private Text word = new Text();
        private boolean hasPair = false;
        private int lastPairKey = 0;

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            IntWritable pairKey;


            word.set(tokenizer.nextToken());
            String parent = tokenizer.nextToken();

            if (!hasPair) {
                pairKey = new IntWritable(lastPairKey);
                hasPair = true;
            }
            else {
                pairKey = new IntWritable(lastPairKey++);
                hasPair = false;
            }

            output.collect( pairKey, new Text(parent));
        }
    }

    public static class CrossOverReduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        private int itemKey;
        public void reduce (IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException{
            StringBuilder firstSon = new StringBuilder();
            StringBuilder secondSon = new StringBuilder();

            Random rand = new Random();
            String firstParent, secondParent;

            if (values.hasNext())
                firstParent = values.next().toString();
            else
                firstParent = null;

            if (values.hasNext())
                secondParent = values.next().toString();
            else
                secondParent = null;

            int cuttingPoint = rand.nextInt(Utils.ITEM_COUNT);

            if (firstParent != null && secondParent != null) {

                output.collect(new IntWritable(itemKey++), new Text(firstParent));
                output.collect(new IntWritable(itemKey++), new Text(secondParent));

                if (rand.nextFloat() <= Utils.CROSSOVER_PROBABILITY) {
                    String chromosomeListP1[] = firstParent.split(";");
                    String chromosomeListP2[] = secondParent.split(";");

                    for (int i = 0; i < Utils.ITEM_COUNT; i++) {
                        if (i < cuttingPoint) {
                            firstSon.append(chromosomeListP1[i]);
                            secondSon.append(chromosomeListP2[i]);
                        } else {
                            firstSon.append(chromosomeListP2[i]);
                            secondSon.append(chromosomeListP1[i]);
                        }

                        if (i != Utils.ITEM_COUNT - 1) {
                            firstSon.append(";");
                            secondSon.append(";");
                        }

                    }

                    output.collect(new IntWritable(itemKey++), new Text(firstSon.toString()));
                    output.collect(new IntWritable(itemKey++), new Text(secondSon.toString()));
                }
            }
        }
    }
}
