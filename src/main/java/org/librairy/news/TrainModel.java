package org.librairy.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang.StringUtils;
import org.librairy.service.learner.facade.rest.model.Document;
import org.librairy.service.learner.facade.rest.model.ModelParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class TrainModel {

    private static final Logger LOG = LoggerFactory.getLogger(TrainModel.class);


    static{
        Unirest.setDefaultHeader("Accept", "application/json");
        Unirest.setDefaultHeader("Content-Type", "application/json");

        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        jacksonObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new ObjectMapper() {

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public static void main(String[] args) throws IOException, UnirestException {


        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(CreateTrainingCorpus.outputFilePath))));

        String endpoint = "http://localhost:7777";
        String user     = "user1";
        String pwd      = "pwd1";


        HttpResponse<String> response = Unirest.delete(endpoint + "/documents").basicAuth(user, pwd).asString();

        String line = null;
        AtomicInteger counter = new AtomicInteger();
        while(!Strings.isNullOrEmpty(line = reader.readLine())){

            String[] values = line.split(CreateTrainingCorpus.separator);

            try{
                Document document = new Document();
                document.setId(String.valueOf(counter.getAndIncrement()));
                document.setName(values[0]);
                document.setLabels(Arrays.asList(new String[]{values[1]}));
                document.setText(StringUtils.substringAfter(values[2],"writes:").replace(">",""));


                response = Unirest.post(endpoint + "/documents").basicAuth(user, pwd).body(document).asString();

                LOG.info("["+(counter.get()-1)+"] "+ response.getStatus() + "::" + response.getStatusText() + " -> " + response.getBody());
            }catch (Exception e){
                LOG.error("Error reading document",e);
            }
        }
        ModelParameters modelParameters = new ModelParameters();
        Map<String, String> parameters = ImmutableMap.of(
                "algorithm","llda",
                "language","en"
        );
        modelParameters.setParameters(parameters);

        response = Unirest.post(endpoint + "/dimensions").basicAuth(user, pwd).body(modelParameters).asString();

        LOG.info("Training model. " + response.getStatus()+ "::" + response.getStatusText() + " -> " + response.getBody());



    }

}
