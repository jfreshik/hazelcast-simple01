package com.example.cache.manual;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandResponse {

    private String value;

    public CommandResponse(String value) {
        this.value = value;
    }
}
