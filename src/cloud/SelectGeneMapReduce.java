package cloud;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SelectGeneMapReduce {
    public static class SelectGeneMap extends MapReduceBase implements Mapper<LongWritable, Text, IntWritable, Text> {
        Logger logger = LoggerFactory.getLogger(SelectGeneMap.class);
        int selectionProbability = ThreadLocalRandom.current().nextInt(Utils.MAXIMUM_WEIGHT / 6);

        public void map(LongWritable key, Text value, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);

            tokenizer.nextToken();
            String gene = tokenizer.nextToken();
            logger.info("Gene: "+gene);
            int fitness = Utils.calculateFitness(gene);
            logger.info("Fitness: "+fitness);
            if ( fitness > selectionProbability ){
                output.collect(new IntWritable(Utils.SURVIVES_KEY), new Text(gene));
            } else {
                output.collect(new IntWritable(Utils.PERISHES_KEY), new Text(gene));
            }

        }
    }

    public static class SelectGeneReduce extends MapReduceBase implements Reducer<IntWritable, Text, IntWritable, Text> {
        Logger logger = LoggerFactory.getLogger(SelectGeneReduce.class);

        public void reduce (IntWritable key, Iterator<Text> values, OutputCollector<IntWritable, Text> output, Reporter reporter) throws IOException{
            List<String> selectedGenes = new ArrayList<String>();

            if ( Utils.SURVIVES_KEY == key.get() ) {
                while (values.hasNext()) {
                    String nextValue = values.next().toString();
                    selectedGenes.add(nextValue);
                }

                logger.info("Values: "+ selectedGenes.size());

                if (selectedGenes.size() % 2 != 0) {
                    Random random = ThreadLocalRandom.current();
                    int clonedGeneIndex = random.nextInt(selectedGenes.size());

                    selectedGenes.add(selectedGenes.get(clonedGeneIndex));
                }

                int geneKey = 0;
                for (String gene : selectedGenes) {
                    logger.info("GeneKey: "+ geneKey +" Gene:"+gene);
                    output.collect(new IntWritable(geneKey), new Text(gene));
                    geneKey++;
                }
            }
        }
    }
}
