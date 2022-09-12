package com.tinkoff.translator;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tinkoff.translator.db.JDBCUtils;
import com.tinkoff.translator.model.JSON;
import com.tinkoff.translator.model.JsonRequest;
import com.tinkoff.translator.model.Request;
import com.tinkoff.translator.model.Word;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.*;


@AllArgsConstructor
@NoArgsConstructor
public class StringTranslator {

    @JsonProperty("translated-string")
    private String result;

    public StringTranslator(JSON json, Request request, String key, String folderId) throws IOException {

        String[] lang = json.getLanguage().split("-");

        CopyOnWriteArrayList<String> listOfWords = new CopyOnWriteArrayList<>(Arrays.asList(json.getString().split(" ")));
        String[] str = new String[listOfWords.size()];
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < listOfWords.size(); i++) {
            Word wordObj = new Word(request.getId(), listOfWords.get(i), null);
            JsonRequest jsonRequest = new JsonRequest(lang[0], lang[1], listOfWords.get(i), folderId);
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(jsonRequest);
            int finalI = i;
            Runnable r = () -> {
                try {
                    wordObj.setWordTranslated(translateWord(key, jsonString));
                    str[finalI] = wordObj.getWordTranslated();
                    try {
                        JDBCUtils.insertWordsQuery(wordObj);

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (finalI == listOfWords.size() - 1) {

                    service.shutdown();

                }

            };


            service.execute(r);


        }//end for
        while (true) {
            if (service.isShutdown()) break;
        }


        request.setOutputString(String.join(" ", str));
        try {
            JDBCUtils.updateRequestQuery(request);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.result = request.getOutputString();

    }

    private String translateWord(String key, String jsonString) throws IOException, InterruptedException {

        URL url = new URL("https://translate.api.cloud.yandex.net/translate/v2/translate");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", " Bearer " + key);
        conn.setRequestProperty("Content-Type", "application/json");

        StringBuilder result = new StringBuilder();
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);

            os.write(input, 0, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectNode node = new ObjectMapper().readValue(result.toString(), ObjectNode.class);


        String translatedWord = node.findValue("text").toString();

        Thread.sleep(500);
        return translatedWord.substring(1, translatedWord.length() - 1);
    }


}
