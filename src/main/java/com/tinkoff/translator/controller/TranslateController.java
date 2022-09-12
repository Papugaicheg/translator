package com.tinkoff.translator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkoff.translator.StringTranslator;
import com.tinkoff.translator.db.JDBCUtils;
import com.tinkoff.translator.model.JSON;
import com.tinkoff.translator.model.Request;
import com.tinkoff.translator.services.IAMTokenRequester;
//import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

@RestController
@PropertySource("classpath:/application.properties")
public class TranslateController {

    //@Value("${api-iam-token}")
    private final String apiKey= IAMTokenRequester.excCommand();

    @Value("${folderId}")
    private String folderId;

    @PostMapping(value = "/translate", produces = "application/json", consumes = "application/json")
    @ResponseBody
    public StringTranslator getTranslate(@RequestBody String jsonRequest, HttpServletRequest httpRequest) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JSON json = objectMapper.readValue(jsonRequest, JSON.class);
        Date date = new Date(Calendar.getInstance().getTimeInMillis());
        Request request = new Request(1, json.getString(), null, date, json.getLanguage(), httpRequest.getRemoteAddr());
        JDBCUtils.insertRequestQuery(request);

        return new StringTranslator(json, request, apiKey, folderId);
    }
}
