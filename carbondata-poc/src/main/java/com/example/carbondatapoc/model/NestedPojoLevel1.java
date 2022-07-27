package com.example.carbondatapoc.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NestedPojoLevel1 {
    private String stringField;
    private int intField;
    private NestedPojoLevel2 complexField;
}
