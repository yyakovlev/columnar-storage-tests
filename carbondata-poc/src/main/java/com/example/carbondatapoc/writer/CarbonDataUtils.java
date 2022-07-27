package com.example.carbondatapoc.writer;

import java.io.IOException;
import java.util.Arrays;

import org.apache.carbondata.common.exceptions.sql.InvalidLoadOptionException;
import org.apache.carbondata.core.metadata.datatype.*;
import org.apache.carbondata.core.scan.expression.ColumnExpression;
import org.apache.carbondata.core.scan.expression.LiteralExpression;
import org.apache.carbondata.core.scan.expression.conditional.EqualToExpression;
import org.apache.carbondata.core.util.CarbonProperties;
import org.apache.carbondata.sdk.file.CarbonWriter;
import org.apache.carbondata.sdk.file.CarbonWriterBuilder;
//import org.apache.carbondata.sdk.file.Field;

import org.apache.carbondata.sdk.file.Schema;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.carbondata.core.constants.CarbonCommonConstants;
import org.apache.carbondata.core.metadata.datatype.DataTypes;
import org.apache.carbondata.core.metadata.datatype.Field;
import org.apache.carbondata.sdk.file.CarbonReader;
import org.apache.carbondata.sdk.file.CarbonSchemaReader;

import org.apache.commons.io.FileUtils;


// See Carbondata examples at:
// https://github.com/apache/carbondata/blob/master/examples/spark/src/main/java/org/apache/carbondata/examples/sdk/CarbonReaderExample.java
// https://github.com/apache/carbondata/blob/master/examples/spark/src/main/java/org/apache/carbondata/examples/sdk/SDKS3Example.java
// Same for parquet:
// https://github.com/contactsunny/Parquet_File_Writer_POC
public class CarbonDataUtils {

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

    public static void testReader() {
        String path = "./testWriteFiles";
        try {
            FileUtils.deleteDirectory(new File(path));
            CarbonProperties.getInstance()
                    .addProperty(CarbonCommonConstants.CARBON_TIMESTAMP_FORMAT,
                            CarbonCommonConstants.CARBON_TIMESTAMP_DEFAULT_FORMAT)
                    .addProperty(CarbonCommonConstants.CARBON_DATE_FORMAT,
                            CarbonCommonConstants.CARBON_DATE_DEFAULT_FORMAT);
            Field[] fields = new Field[12];
            fields[0] = new Field("stringField", DataTypes.STRING);
            fields[1] = new Field("shortField", DataTypes.SHORT);
            fields[2] = new Field("intField", DataTypes.INT);
            fields[3] = new Field("longField", DataTypes.LONG);
            fields[4] = new Field("doubleField", DataTypes.DOUBLE);
            fields[5] = new Field("boolField", DataTypes.BOOLEAN);
            fields[6] = new Field("dateField", DataTypes.DATE);
            //fields[7] = new Field("timeField", DataTypes.TIMESTAMP);
            fields[7] = new Field("anotherStringField", DataTypes.STRING);

            fields[8] = new Field("decimalField", DataTypes.createDecimalType(8, 2));
            fields[9] = new Field("varcharField", DataTypes.VARCHAR);
            fields[10] = new Field("arrayField", DataTypes.createArrayType(DataTypes.STRING));
            fields[11] = new Field("complexField", DataTypes.createStructType(
                    Arrays.asList(new StructField("structStringField", DataTypes.STRING),
                            new StructField("structLongField", DataTypes.LONG))));

            CarbonWriter writer = CarbonWriter.builder()
                    .outputPath(path)
                    .withLoadOption("complex_delimiter_level_1", "#")
                    .withCsvInput(new Schema(fields))
                    .writtenBy("CarbonReaderExample")
                    .build();

            for (int i = 0; i < 10; i++) {
                String[] row2 = new String[]{
                        "robot" + (i % 10),
                        String.valueOf(i % 10000),
                        String.valueOf(i),
                        String.valueOf(Long.MAX_VALUE - i),
                        String.valueOf((double) i / 2),
                        String.valueOf(true),
                        "2019-03-02",
                        "2019-02-12 03:03:34",
                        "12.345",
                        "varchar",
                        "Hello#World#From#Carbon",
                        "str#555"
                };
                writer.write(row2);
            }
            writer.close();

            File[] dataFiles = new File(path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name == null) {
                        return false;
                    }
                    return name.endsWith("carbonindex");
                }
            });
            if (dataFiles == null || dataFiles.length < 1) {
                throw new RuntimeException("Carbon index file not exists.");
            }
            Schema schema = CarbonSchemaReader
                    .readSchema(dataFiles[0].getAbsolutePath())
                    .asOriginOrder();
            // Transform the schema
            String[] strings = new String[schema.getFields().length];
            for (int i = 0; i < schema.getFields().length; i++) {
                strings[i] = (schema.getFields())[i].getFieldName();
            }

            // Read data

            EqualToExpression equalToExpression = new EqualToExpression(
                    new ColumnExpression("intField", DataTypes.INT),
                    new LiteralExpression(5, DataTypes.INT));
            CarbonReader<Object[]> reader = CarbonReader
                    .builder(path, "_temp")
                    .projection(strings)
                    .filter(equalToExpression)
                    .build();

            System.out.println("\nData:");
            int i = 0;
            while (reader.hasNext()) {
                Object[] row = (Object[]) reader.readNextRow();
                System.out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t",
                        i, row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7],
                        row[8], row[9]
                ));
                Object[] arr = (Object[]) row[10];
                for (int j = 0; j < arr.length; j++) {
                    System.out.print(arr[j] + " ");
                }
                assert (arr[0].equals("Hello"));
                assert (arr[3].equals("Carbon"));
                System.out.println();
                i++;
            }
            reader.close();

            // Read data
            CarbonReader<Object[]> reader2 = CarbonReader
                    .builder(path, "_temp")
                    .build();

            System.out.println("\nData:");
            i = 0;
            while (reader2.hasNext()) {
                Object[] row = (Object[]) reader2.readNextRow();
                System.out.print(String.format("%s\t%s\t%s\t%s\t%s\t",
                        i, row[0], row[1], row[2],
                        row[3]));
                Object[] arr = (Object[]) row[4];
                for (int j = 0; j < arr.length; j++) {
                    System.out.print(arr[j] + " ");
                }
                System.out.println(String.format("\t%s\t%s\t%s\t%s\t%s\t%s\t",
                        row[5], row[6], row[7], row[8], row[9], row[10]));
                i++;
            }
            reader2.close();
        } catch (Throwable e) {
            e.printStackTrace();
            assert (false);
            System.out.println(e.getMessage());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
