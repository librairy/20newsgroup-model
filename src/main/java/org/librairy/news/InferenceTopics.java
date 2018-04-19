package org.librairy.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.librairy.news.model.JsonRow;
import org.librairy.service.learner.facade.rest.model.Document;
import org.librairy.service.learner.facade.rest.model.ModelParameters;
import org.librairy.service.modeler.facade.rest.model.ShapeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Badenes Olmedo, Carlos <cbadenes@fi.upm.es>
 */

public class InferenceTopics {

    private static final Logger LOG = LoggerFactory.getLogger(InferenceTopics.class);

    static String outputFilePath = "src/main/resources/reuters21578-topics-from-20newsgroup.jsonl.gz";

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


        File outputFile = new File(outputFilePath);
        if (outputFile.exists()) outputFile.delete();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outputFile, false))));
        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(CreateInferenceCorpus.outputFilePath))));

        String endpoint6    = "http://localhost:7780";
        String endpoint20   = "http://localhost:7781";
        String endpoint60   = "http://localhost:7782";
        String user     = "user1";
        String pwd      = "pwd1";



        String line = null;
        AtomicInteger counter = new AtomicInteger();
        com.fasterxml.jackson.databind.ObjectMapper jsonMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        HttpResponse<JsonNode> response;
        while(!Strings.isNullOrEmpty(line = reader.readLine())){

            String[] values = line.split(CreateInferenceCorpus.separator);

            JsonRow jsonRow = new JsonRow();
            try{
                String name = values[0];
                jsonRow.setName(name);
                String date = values[1];
                jsonRow.setDate(date);
                String text = values[2];
                jsonRow.setText(text);

                ShapeRequest request = new ShapeRequest();
                request.setText(values[2]);

                response = Unirest.post(endpoint6 + "/shape").basicAuth(user, pwd).body(request).asJson();
                JSONArray vector6 = response.getBody().getObject().getJSONArray("vector");
                jsonRow.setGeneral_0(vector6.getDouble(0));
                jsonRow.setGeneral_1(vector6.getDouble(1));
                jsonRow.setGeneral_2(vector6.getDouble(2));
                jsonRow.setGeneral_3(vector6.getDouble(3));
                jsonRow.setGeneral_4(vector6.getDouble(4));
                jsonRow.setGeneral_5(vector6.getDouble(5));

                response = Unirest.post(endpoint20 + "/shape").basicAuth(user, pwd).body(request).asJson();
                JSONArray vector20 = response.getBody().getObject().getJSONArray("vector");
                jsonRow.setSport_hockey(vector20.getDouble(0));
                jsonRow.setReligion_atheism(vector20.getDouble(1));
                jsonRow.setScience_space(vector20.getDouble(2));
                jsonRow.setScience_medicine(vector20.getDouble(3));
                jsonRow.setPolitics_misc(vector20.getDouble(4));
                jsonRow.setComputer_mac_hw(vector20.getDouble(5));
                jsonRow.setPoliticis_mnideast(vector20.getDouble(6));
                jsonRow.setComputer_ibm_hw(vector20.getDouble(7));
                jsonRow.setForsale(vector20.getDouble(8));
                jsonRow.setScience_electronics(vector20.getDouble(9));
                jsonRow.setComputer_windows_misc(vector20.getDouble(10));
                jsonRow.setMotor_motorcycle(vector20.getDouble(11));
                jsonRow.setSport_baseball(vector20.getDouble(12));
                jsonRow.setReligion_christian(vector20.getDouble(13));
                jsonRow.setPolitics_gun(vector20.getDouble(14));
                jsonRow.setComputer_graphics(vector20.getDouble(15));
                jsonRow.setMotor_autos(vector20.getDouble(16));
                jsonRow.setReligion_misc(vector20.getDouble(17));
                jsonRow.setComputer_windows_x(vector20.getDouble(18));
                jsonRow.setScience_crypt(vector20.getDouble(19));

                response = Unirest.post(endpoint60 + "/shape").basicAuth(user, pwd).body(request).asJson();
                JSONArray vector60 = response.getBody().getObject().getJSONArray("vector");
                jsonRow.setSpecific_0(vector60.getDouble(0));
                jsonRow.setSpecific_1(vector60.getDouble(1));
                jsonRow.setSpecific_2(vector60.getDouble(2));
                jsonRow.setSpecific_3(vector60.getDouble(3));
                jsonRow.setSpecific_4(vector60.getDouble(4));
                jsonRow.setSpecific_5(vector60.getDouble(5));
                jsonRow.setSpecific_6(vector60.getDouble(6));
                jsonRow.setSpecific_7(vector60.getDouble(7));
                jsonRow.setSpecific_8(vector60.getDouble(8));
                jsonRow.setSpecific_9(vector60.getDouble(9));
                jsonRow.setSpecific_10(vector60.getDouble(10));
                jsonRow.setSpecific_11(vector60.getDouble(11));
                jsonRow.setSpecific_12(vector60.getDouble(12));
                jsonRow.setSpecific_13(vector60.getDouble(13));
                jsonRow.setSpecific_14(vector60.getDouble(14));
                jsonRow.setSpecific_15(vector60.getDouble(15));
                jsonRow.setSpecific_16(vector60.getDouble(16));
                jsonRow.setSpecific_17(vector60.getDouble(17));
                jsonRow.setSpecific_18(vector60.getDouble(18));
                jsonRow.setSpecific_19(vector60.getDouble(19));
                jsonRow.setSpecific_20(vector60.getDouble(20));
                jsonRow.setSpecific_21(vector60.getDouble(21));
                jsonRow.setSpecific_22(vector60.getDouble(22));
                jsonRow.setSpecific_23(vector60.getDouble(23));
                jsonRow.setSpecific_24(vector60.getDouble(24));
                jsonRow.setSpecific_25(vector60.getDouble(25));
                jsonRow.setSpecific_26(vector60.getDouble(26));
                jsonRow.setSpecific_27(vector60.getDouble(27));
                jsonRow.setSpecific_28(vector60.getDouble(28));
                jsonRow.setSpecific_29(vector60.getDouble(29));
                jsonRow.setSpecific_30(vector60.getDouble(30));
                jsonRow.setSpecific_31(vector60.getDouble(31));
                jsonRow.setSpecific_32(vector60.getDouble(32));
                jsonRow.setSpecific_33(vector60.getDouble(33));
                jsonRow.setSpecific_34(vector60.getDouble(34));
                jsonRow.setSpecific_35(vector60.getDouble(35));
                jsonRow.setSpecific_36(vector60.getDouble(36));
                jsonRow.setSpecific_37(vector60.getDouble(37));
                jsonRow.setSpecific_38(vector60.getDouble(38));
                jsonRow.setSpecific_39(vector60.getDouble(39));
                jsonRow.setSpecific_40(vector60.getDouble(40));
                jsonRow.setSpecific_41(vector60.getDouble(41));
                jsonRow.setSpecific_42(vector60.getDouble(42));
                jsonRow.setSpecific_43(vector60.getDouble(43));
                jsonRow.setSpecific_44(vector60.getDouble(44));
                jsonRow.setSpecific_45(vector60.getDouble(45));
                jsonRow.setSpecific_46(vector60.getDouble(46));
                jsonRow.setSpecific_47(vector60.getDouble(47));
                jsonRow.setSpecific_48(vector60.getDouble(48));
                jsonRow.setSpecific_49(vector60.getDouble(49));
                jsonRow.setSpecific_50(vector60.getDouble(50));
                jsonRow.setSpecific_51(vector60.getDouble(51));
                jsonRow.setSpecific_52(vector60.getDouble(52));
                jsonRow.setSpecific_53(vector60.getDouble(53));
                jsonRow.setSpecific_54(vector60.getDouble(54));
                jsonRow.setSpecific_55(vector60.getDouble(55));
                jsonRow.setSpecific_56(vector60.getDouble(56));
                jsonRow.setSpecific_57(vector60.getDouble(57));
                jsonRow.setSpecific_58(vector60.getDouble(58));
                jsonRow.setSpecific_59(vector60.getDouble(59));


                // write json
                String json = jsonMapper.writeValueAsString(jsonRow);
                writer.write(json+"\n");

                LOG.info("Document '" + name+"' annotated");

            }catch (Exception e){
                LOG.error("Error reading document",e);
            }
        }
        writer.close();

        LOG.info("All documents annotated");



    }

}
