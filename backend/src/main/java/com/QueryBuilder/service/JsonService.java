package com.QueryBuilder.service;
import com.QueryBuilder.controller.Query;
import com.QueryBuilder.dto.MethodConfigDTO;
import com.QueryBuilder.dto.RequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JsonService {


    public  ObjectMapper objectMapper;

    public Map<String, String> tableNames = new HashMap<String, String>();


    public JsonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        tableNames.put("blockConfig", "m_block_config");
        tableNames.put("methodVariablesList", "m_methods_variables");
        tableNames.put("methodStatementDetailList", "m_method_statement_detail");
        tableNames.put("methodStatementList", "m_method_statement");
        tableNames.put("expressionConfig", "m_expression_config");
        tableNames.put("operand", "m_operand_config");
        tableNames.put("operator", "config_m_operators");
        tableNames.put("methodArgumentsConfigList", "m_method_arguments_config");
        tableNames.put("methodVariables", "m_method_variables");
        tableNames.put("parent", "m_method_config");
        tableNames.put("leftOperand", "m_operand_config");
        tableNames.put("rightOperand", "m_operand_config");

    }

    public List<MethodConfigDTO> mapJsonToDTO(String jsonString) throws IOException {
        TypeReference<List<MethodConfigDTO>> typeReference = new TypeReference<List<MethodConfigDTO>>() {
        };
        return objectMapper.readValue(jsonString, typeReference);
    }



    public void getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {
        StringBuilder query;

        for (MethodConfigDTO method : methodConfigDTOList) {
            generateScript(method, tableNames.get("parent"));
        }

    }


private UUID generateScript(Object method, String tableName) throws IllegalAccessException {
    StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");

    // getting fields in a method
    Field[] fields = method.getClass().getDeclaredFields();

    // Loop fields to get column names
    for (int i = 0; i < fields.length; i++) {
        fields[i].setAccessible(true);
        //System.out.println("Fields["+i+"]"+fields[i].getType());

        if (!List.class.isAssignableFrom(fields[i].getType())) {
            query.append(fields[i].getName());
            if (i != fields.length - 1) query.append(" ,");

        }
//        if (i != fields.length - 1) query.append(" ,");
        //query.append(fields[i].getName());

    }

//    if (query.length() > 1) {
//        query.setLength(query.length() - 1);
//    }
    query.append(" ) VALUES (");

    // Loop to find values
    for (int i = 0; i < fields.length; i++) {
        Object value = fields[i].get(method);
        Class<?> fieldType = fields[i].getType();
        if (value == null) {
            if (!List.class.isAssignableFrom(fieldType)) {
                query.append(value);
                if (i != fields.length - 1) query.append(" ,");
            }
        } else if (fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == UUID.class) {
            // Handle primitive types or String or subclasses of Number
            query.append(value );
            if (i != fields.length - 1) query.append(" ,");
        } else {
            // Handle nested structures
            if (List.class.isAssignableFrom(value.getClass())) {
                List<?> list = (List<?>) value;
                //System.out.println("list----"+list);
                for (Object listItem : list) {
                    //System.out.println("List Item :"+listItem);
                    if (listItem != null) {
                        String table = tableNames.get(fields[i].getName());
                        UUID id = generateScript(listItem, table);

//                        query.append(id+" ");
                    }
                }
            } else {
                // Recursively handle nested objects
                String table = tableNames.get(fields[i].getName());
                UUID id = generateScript(value, table);
                query.append(id + " ");
                if (i != fields.length - 1) query.append(" ,");
            }


        }
    }

//    if (query.length() > 2) {
//        query.setLength(query.length() - 1);
//    }
    query.append(")");

    System.out.println(query.toString());
    return (UUID) fields[0].get(method);
}

}


