package com.example.carbondatapoc.reader;

import com.example.carbondatapoc.StatisticsCollector;
import lombok.extern.slf4j.Slf4j;
import org.apache.carbondata.core.scan.expression.Expression;
import org.apache.carbondata.sdk.file.CarbonReader;
import org.apache.carbondata.sdk.file.CarbonReaderBuilder;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.reflect.FieldUtils;

@Slf4j
@Component
public class CarbonFileReader {

    @Autowired
    private ConversionService conversionService;

    public <T> List<T> read(String path, @Nullable Expression expression,
            Class<T> pojoClass, StatisticsCollector statisticsCollector) {

        CarbonReaderBuilder builder = CarbonReader.builder(path, "_temp");
        if (expression != null) {
            builder = builder.filter(expression);
        }
        try {
            CarbonReader<Object[]> reader = builder.build();
            List<java.lang.reflect.Field> fields;
            try {
                fields = FieldUtils.getAllFieldsList(pojoClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            while (reader.hasNext()) {
                Object[] row = (Object[]) reader.readNextRow();
                T pojo = pojoClass.newInstance();
                for (int i = 0; i < row.length; i++) {
                    java.lang.reflect.Field field = fields.get(i);
                    Class<?> fieldClass = field.getType();
                    String fieldClassString = fieldClass.getSimpleName();
                    Object value = null;
                    if (conversionService.canConvert(row[i].getClass(), fieldClass)) {
                        value = conversionService.convert(row[i], fieldClass);
                        try {
                            BeanUtils.setProperty(pojo, field.getName(), value);
                        } catch (IllegalAccessException|InvocationTargetException e) {
                            throw new RuntimeException("Could not set {} to {}", e);
                        } 
                    }
                    
                    //  switch (fieldClassString) {
                    //      case String.class:
                    //      BeanUtils.setPro

                    // }
                    log.info("{} : type '{}', value {}", i, fieldClassString, row[i]);
                }
                

                for (Object column : row) {
                    // TODO map to object fields
                }
                // System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t",
                // i, row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7],
                // row[8], row[9]
                // ));
                // Object[] arr = (Object[]) row[10];
                // for (int j = 0; j < arr.length; j++) {
                // System.out.print(arr[j] + " ");
                // }
                // assert (arr[0].equals("Hello"));
                // assert (arr[3].equals("Carbon"));
                // System.out.println();
                // i++;
                log.info("Read row: {}", ArrayUtils.toString(row));
            }
            reader.close();

        } catch (IOException | InterruptedException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Could not read", e);
        }
        return Arrays.asList();
    }
}
