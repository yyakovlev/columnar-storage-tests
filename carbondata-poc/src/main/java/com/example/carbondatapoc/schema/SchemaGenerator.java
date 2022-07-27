package com.example.carbondatapoc.schema;

import org.apache.carbondata.core.metadata.datatype.DataType;
import org.apache.carbondata.core.metadata.datatype.DataTypes;
import org.apache.carbondata.core.metadata.datatype.Field;
import org.apache.carbondata.sdk.file.Schema;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchemaGenerator {

    public <T> Schema carbonSchema(Class<T> pojoClass) {
        try {
            //Map<String, String> properties = BeanUtils.describe(pojo);
            List<java.lang.reflect.Field> fields = FieldUtils.getAllFieldsList(pojoClass);
            Field[] carbonFields = fields.stream().map(this::toCarbonField).collect(Collectors.toList()).toArray(new Field[0]);
            return new Schema(carbonFields);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Field toCarbonField(java.lang.reflect.Field field) {
        String fieldName = field.getName();
        DataType type = DataTypes.STRING;
        return new Field(fieldName, type);
    }
}
