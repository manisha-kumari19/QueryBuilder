package com.QueryBuilder.service;

import com.QueryBuilder.controller.Query;
import com.QueryBuilder.dto.*;
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


    public ObjectMapper objectMapper;

    public Map<String, String> tableNames = new HashMap<String, String>();


    public JsonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        tableNames.put("blockConfig", "m_block_config");
        tableNames.put("methodVariablesList", "m_method_variables");
        tableNames.put("methodStatementDetailList", "m_method_statement_detail");
        tableNames.put("methodStatementList", "m_method_statement");
        tableNames.put("expressionConfig", "m_expression_config");
        tableNames.put("operand", "m_operand_config");
        tableNames.put("operator", "m_operators");
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
        StringBuilder query = new StringBuilder("INSERT INTO config." + tableName + " (");

        // getting fields in a method
        Field[] fields = method.getClass().getDeclaredFields();

        // Loop fields to get column names
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);

            Class<?> fieldType = fields[i].getType();

            if (fields[i].getName().equals("package_name")) {
                // Handle "package_name" attributes separately
                query.append("package_name");
            } else if (fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == UUID.class) {
                query.append(fields[i].getName());
                if (i != fields.length - 1) query.append(" ,");
            }
        }
        if(query.charAt(query.length()-1) == ',')
         {
            query.deleteCharAt(query.length()-1);
         }
        query.append(") VALUES (");

        // Loop to find values
        for (int i = 0; i < fields.length; i++) {
            Object value = fields[i].get(method);
            Class<?> fieldType = fields[i].getType();


            if (PackageNameDTO.class.isAssignableFrom(fieldType)) {
                if (value != null) {
                    PackageNameDTO packageNameDTO = (PackageNameDTO) value;
                    query.append("'"+packageNameDTO.getValue()+"'");
                } else {
                    query.append("NULL");
                }
            } else if (value == null) {
                if (    !List.class.isAssignableFrom(fieldType)
                        && !BlockConfigDTO.class.isAssignableFrom(fieldType)
                        && !ExpressionConfigDTO.class.isAssignableFrom(fieldType)
                        && !MethodArgumentsConfigDTO.class.isAssignableFrom(fieldType)
                        && !MethodConfigDTO.class.isAssignableFrom(fieldType)
                        && !MethodStatementDetailDTO.class.isAssignableFrom(fieldType)
                        && !MethodStatementDTO.class.isAssignableFrom(fieldType)
                        && !MethodVariableDTO.class.isAssignableFrom(fieldType)
                        && !OperandConfigDTO.class.isAssignableFrom(fieldType)
                        && !OperatorDTO.class.isAssignableFrom(fieldType)

                ) {
                    query.append("NULL");
                    if (i != fields.length - 1) query.append(",");
                }
            } else if (fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == UUID.class) {
                // Handle primitive types or String or subclasses of Number
                if(fieldType==String.class || fieldType == UUID.class || fieldType == Character.class)
                {
                    query.append("'"+value+"'");
                }
                else query.append(value);
                if (i != fields.length - 1) query.append(",");
            } else {
                // Handle nested structures
                if (List.class.isAssignableFrom(value.getClass())) {
                    List<?> list = (List<?>) value;

                    for (Object listItem : list) {

                        if (listItem != null) {
                            String table = tableNames.get(fields[i].getName());
                            UUID id = generateScript(listItem, table);
                        }
                    }
                } else {
                    // Recursively handle nested objects
                    String table = tableNames.get(fields[i].getName());
                    UUID id = generateScript(value, table);
                }
            }
        }

        if(query.charAt(query.length()-1) == ',')
        {
            query.deleteCharAt(query.length()-1);
        }
        query.append(");");

        System.out.println(query.toString());
        return (UUID) fields[0].get(method);
    }

}


