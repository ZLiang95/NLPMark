package NLP;
import NLPMarkTool.*;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException{
        FormatConversion.CSVtoTSV("./data/articles.csv","./data/articles.tsv");

        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,cleanxml,ssplit,pos,lemma,ner,depparse,parse");
        props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/english-left3words-distsim.tagger");
        props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/english_UD.gz");
        props.setProperty("parse.maxlen", "100");
        props.setProperty("ssplit.boundaryTokenRegex", "[.]|[!?]+|[。]|[！？]+");
        props.setProperty("threads","1");

        //articles是二维数组，n*2,n行2列
        ArrayList<List<String>> articles = TsvParser.getTsv("./data/articles.tsv");

        SparkConf conf=new SparkConf().setMaster("local[2]").setAppName("NLPMark");
        //使用Kryo序列化方式
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
        JavaSparkContext sc=new JavaSparkContext(conf);

        //使用文件读取的数据创建RDD
        JavaRDD<List<String>> lines=sc.parallelize(articles,2);
        DocunmentParse docunmentParse = new DocunmentParse(props);

        JavaRDD<Sentence> resultRDD = lines.flatMap(line->
            docunmentParse.getParseResults(line.get(0), Utils.cleanTxt(line.get(1))).iterator()
        );
        List<Sentence> parseResults = resultRDD.collect();
        sc.stop();

        for(Sentence result : parseResults){
            TsvParser.saveToTsv(result, "./data/sentences.tsv");
        }
        System.out.println("SUCCESS!");
    }
}
