package org.librairy.news;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */
public class CreateTrainingCorpus {

    private static final Logger LOG = LoggerFactory.getLogger(CreateTrainingCorpus.class);

    static Map<String,String> categories = new HashMap<>();

    static String baseDir          = "/Users/cbadenes/Corpus/20newsgroups";
    static String outputFilePath   = "src/main/resources/20newsgroup.csv.gz";
    static String separator        = ";;";


    public static void main(String[] args) throws IOException {

        categories.put("religion_atheism","alt.atheism");
        categories.put("computer_graphics","comp.graphics");
        categories.put("computer_windows_misc","comp.os.ms-windows.misc");
        categories.put("computer_ibm_hw","comp.sys.ibm.pc.hardware");
        categories.put("computer_mac_hw","comp.sys.mac.hardware");
        categories.put("computer_windows_x","comp.windows.x");
        categories.put("forsale","misc.forsale");
        categories.put("motor_autos","rec.autos");
        categories.put("motor_motorcycle","rec.motorcycles");
        categories.put("sport_baseball","rec.sport.baseball");
        categories.put("sport_hockey","rec.sport.hockey");
        categories.put("science_crypt","sci.crypt");
        categories.put("science_electronics","sci.electronics");
        categories.put("science_medicine","sci.med");
        categories.put("science_space","sci.space");
        categories.put("religion_christian","soc.religion.christian");
        categories.put("politics_guns","talk.politics.guns");
        categories.put("politics_mideast","talk.politics.mideast");
        categories.put("politics_misc","talk.politics.misc");
        categories.put("religion_misc","talk.religion.misc");





        File outputFile = new File(outputFilePath);
        if (outputFile.exists()) outputFile.delete();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile, false))));

        AtomicInteger counter = new AtomicInteger();
        for (Map.Entry<String,String> entry : categories.entrySet()){



            try(Stream<Path> stream = Files.walk(Paths.get(baseDir,entry.getValue()))) {
                stream.filter(Files::isRegularFile).forEach(filePath -> {

                            try (Stream<String> lines = Files.lines(filePath, Charset.forName("ISO-8859-1"))) {
                                String text = lines.collect(Collectors.joining(" "));

                                if (!Strings.isNullOrEmpty(text)){
                                    StringBuilder row = new StringBuilder();
                                    row.append(filePath.toFile().getName()).append(separator).append(entry.getKey()).append(separator).append(text).append("\n");
                                    writer.write(row.toString());

                                    LOG.info("["+counter.getAndIncrement()+"] Processed:  " + filePath.toFile().getAbsolutePath());
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception e){
                                LOG.error("Invalid file format: " + filePath.toFile().getAbsolutePath(), e);

                                Charset.availableCharsets().forEach( (a,c) -> {
                                    try{
                                        Files.lines(filePath,c);
                                        LOG.info("Charset is " + c);
                                    } catch (Exception e1) {

                                    }
                                });

                            }

                        });
            }
            catch(UncheckedIOException ex) {
                throw ex.getCause();
            }



        }

        writer.close();

    }



}
