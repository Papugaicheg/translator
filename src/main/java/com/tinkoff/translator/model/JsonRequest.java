package com.tinkoff.translator.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonRequest {

    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String texts;
    private String folderId;

}
