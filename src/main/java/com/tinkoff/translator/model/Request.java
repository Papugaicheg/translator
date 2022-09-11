package com.tinkoff.translator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.sql.Date;

@Data
@AllArgsConstructor
@ToString
public class Request {

    private long id; //id запроса
    private String inputString;
    private String outputString;
    private Date date;
    private String languages;
    private String ip;




}
