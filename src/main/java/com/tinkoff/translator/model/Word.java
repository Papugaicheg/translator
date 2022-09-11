package com.tinkoff.translator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {
    private long id;
    private String word;
    private String wordTranslated;
}
