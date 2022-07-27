package com.example.carbondatapoc.writer;

import com.example.carbondatapoc.StatisticsCollector;
import com.example.carbondatapoc.model.MultiTypePojo;
import com.example.carbondatapoc.schema.SchemaGenerator;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.carbondata.common.exceptions.sql.InvalidLoadOptionException;
import org.apache.carbondata.sdk.file.CarbonWriter;
import org.apache.carbondata.sdk.file.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class CarbonFileWriter {

    @Autowired
    private SchemaGenerator schemaGenerator;

    public <T> void write(List<T> pojos, StatisticsCollector statisticsCollector, String path) {
        Schema schema = schemaGenerator.carbonSchema(MultiTypePojo.class);
        log.info("Schema generated : {}", schema);
        CarbonWriter jsonWriter = createJsonCarbonWriter(path, schema);

        Gson gson = new Gson();
        for (T pojo : pojos) {
            String json = gson.toJson(pojo);
            try {
                jsonWriter.write(json);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not write json % to carbondata file", json), e);
            }
        }
        try {
            jsonWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close json writer", e);
        }
    }

    private CarbonWriter createJsonCarbonWriter(String path, Schema schema) {
        try {
            CarbonWriter writer = CarbonWriter.builder()
                    .outputPath(path)
                    .withJsonInput(schema)
                    .writtenBy("columnar-storage-tests")
                    .build();
            return writer;
        } catch (IOException | InvalidLoadOptionException e) {
            throw new RuntimeException("Could not create carbon writer");
        }
    }
}
