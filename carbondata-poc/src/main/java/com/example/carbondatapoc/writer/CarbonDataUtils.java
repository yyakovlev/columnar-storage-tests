package com.example.carbondatapoc.writer;

import java.io.IOException;

import org.apache.carbondata.common.exceptions.sql.InvalidLoadOptionException;
import org.apache.carbondata.core.metadata.datatype.DataTypes;
import org.apache.carbondata.core.util.CarbonProperties;
import org.apache.carbondata.sdk.file.CarbonWriter;
import org.apache.carbondata.sdk.file.CarbonWriterBuilder;
import org.apache.carbondata.core.metadata.datatype.Field;
//import org.apache.carbondata.sdk.file.Field;

import org.apache.carbondata.sdk.file.Schema;


// See Carbondata examples at:
// https://github.com/apache/carbondata/blob/master/examples/spark/src/main/java/org/apache/carbondata/examples/sdk/CarbonReaderExample.java
// https://github.com/apache/carbondata/blob/master/examples/spark/src/main/java/org/apache/carbondata/examples/sdk/SDKS3Example.java
// Same for parquet:
// https://github.com/contactsunny/Parquet_File_Writer_POC
public class CarbonWriterUtils {

    public static void testSdkWriter(String enableOffheap) throws IOException, InvalidLoadOptionException {
        String path = "./target/testCSVSdkWriter";

        Field[] fields = new Field[2];
        fields[0] = new Field("name", DataTypes.STRING);
        fields[1] = new Field("age", DataTypes.INT);

        Schema schema = new org.apache.carbondata.sdk.file.Schema(fields);

        CarbonProperties.getInstance().addProperty("enable.offheap.sort", enableOffheap);

        CarbonWriterBuilder builder = CarbonWriter.builder().outputPath(path).withCsvInput(schema).writtenBy("SDK");

        CarbonWriter writer = builder.build();

        int rows = 5;
        for (int i = 0; i < rows; i++) {
            writer.write(new String[] { "robot" + (i % 10), String.valueOf(i) });
        }
        writer.close();
    }
}
