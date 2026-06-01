package ru.kryuch.krtg.searcher.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Setting implements Serializable {

    private String code;

    private String value;

}
