package com.example.carbondatapoc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiTypePojo {

    private String stringField;
    private short shortField;
    private int intField;
    private long longField;
    private double doubleField;
    private boolean boolField;
    private LocalDate dateField;
    private LocalDateTime timestampField;
    private BigDecimal decimalField;
    //@ConversionHint("VARCHAR")
    private String varcharField;
    private List<String> stringArrayField;
    private NestedPojoLevel1 complexField;
}
