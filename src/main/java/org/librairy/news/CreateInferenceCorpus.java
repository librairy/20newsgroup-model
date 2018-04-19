package org.librairy.news;

import com.google.common.base.Strings;
import org.apache.lucene.benchmark.utils.ExtractReuters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CreateInferenceCorpus {

    private static final Logger LOG = LoggerFactory.getLogger(CreateInferenceCorpus.class);

    static Map<String,String> categories = new HashMap<>();

    static String baseDir          = "/Users/cbadenes/Corpus/reuters21578";
    static String outputFilePath   = "src/main/resources/reuters21578.csv.gz";
    static String separator        = ";;";


    public static void main(String[] args) throws IOException {


        Path filesDirectory = Paths.get(baseDir, "files");

        if (!filesDirectory.toFile().exists()){
            ExtractReuters extractReuters = new ExtractReuters(Paths.get(baseDir),filesDirectory);
            extractReuters.extract();
        }


        File outputFile = new File(outputFilePath);
        if (outputFile.exists()) outputFile.delete();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile, false))));

        AtomicInteger counter = new AtomicInteger();


        List<Path> files = Files.walk(Paths.get(baseDir, "files")).filter(Files::isRegularFile).collect(Collectors.toList());

        for (Path filePath : files){

            try(BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))){
                List<String> text = br.lines().collect(Collectors.toList());
                if (text.size() < 5) continue;
                String date = text.get(0);
                String name = text.get(2);
                String body = text.get(4).replace("Reuter &#3;","");

                StringBuilder row = new StringBuilder();
                row.append(name).append(separator).append(date).append(separator).append(body).append("\n");
                writer.write(row.toString());

                LOG.info("["+counter.getAndIncrement()+"] Processed:  " + filePath.getFileName().toString());

                br.close();
            } catch (IOException e) {
                throw new AssertionError(e);
            }

        }
        writer.close();



    }



}
