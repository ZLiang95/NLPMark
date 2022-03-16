package NLP;

import org.apache.commons.csv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class FormatConversion {
    public static void CSVtoTSV(String path1,String path2) throws IOException {
        //读取CSV文件
        Reader in = new FileReader(path1);
        ArrayList<List<String>> lines = new ArrayList<>();
        final CSVFormat format = CSVFormat.DEFAULT;     //CSV默认格式
        CSVParser parser = CSVParser.parse(in,format);
        for (CSVRecord record : parser) {
            lines.add(record.toList());
        }

        //以TSV文件格式写入磁盘
        CSVFormat.Builder builder = CSVFormat.Builder.create();
        builder.setDelimiter('\t').setAutoFlush(true).setAllowMissingColumnNames(false);
        builder.setQuote(null);
        CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(path2,true), builder.build());
        for(List<String> line:lines){
            csvPrinter.printRecord(line.get(0),line.get(1));
        }
        csvPrinter.flush();
        csvPrinter.close();
    }
}
