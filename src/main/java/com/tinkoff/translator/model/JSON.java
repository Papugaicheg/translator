package com.tinkoff.translator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class JSON {

    @JsonProperty("string")
    String string;
    @JsonProperty("language")
    String language;


}
