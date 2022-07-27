package com.example.carbondatapoc;

import com.example.carbondatapoc.model.MultiTypePojo;
import com.example.carbondatapoc.model.NestedPojoLevel1;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class PojoGenerator {

    public List<MultiTypePojo> generate(Class<MultiTypePojo> pojoClass, int count) {
        return Arrays.asList(
                MultiTypePojo.builder()
                        .stringField("string-000001")
                        .intField(1)
                        .shortField((short)1)
                        .longField(1L)
                        .doubleField(20.5)
                        .decimalField(new BigDecimal("5.5"))
                        .boolField(true)
                        .dateField(LocalDate.now())
                        .timestampField(LocalDateTime.now())
                        .varcharField("*".repeat(100))
                        .stringArrayField(Arrays.asList("A", "B", "C"))
                        .complexField(NestedPojoLevel1.builder().build())
                        .build()
        );
    }
}
