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


    //    public String getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {
//        StringBuilder query;
//
//        for (MethodConfigDTO method : methodConfigDTOList) {
//            query = new StringBuilder();
//            query.append("INSERT INTO config.m_method_config  (");
//
//            //itertation of fields
//            Field[] fields = MethodConfigDTO.class.getDeclaredFields();
//            for (int i = 0; i < fields.length; i++) {
//                if (!fields[i].isAccessible()) {
//                    fields[i].setAccessible(true);
//                }
//                query.append(fields[i].getName());
//                if (i != fields.length - 1) query.append(" ,");
//            }
//            query.append(" ) VALUES (");
////                for (int i = 0; i < fields.length; i++) {
////                    Object value = fields[i].get(method);
////
////                    //System.out.println(value);
////                }
//
//            //for checking type of values
//            for (int i = 0; i < fields.length; i++) {
//                Object value = fields[i].get(method);
//                Class<?> fieldType = fields[i].getType();
//                if (value == null || fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == UUID.class) {
//                    // Handle primitive types or String or subclasses of Number
//                    query.append(value + " ,");
//                    //System.out.println(fields[i].getName() + " " + value + "  " + fields[i].getType() + " " + "true");
//                } else {
//                    //System.out.println(fields[i].getName() + " " + value + "  " + fields[i].getType() + " " + "false");
//                    int num =  generateInnerQuery(value);
//                    query.append("null ,");
//                }
//            }
//            query.append(")");
//            //System.out.println(query);
//        }
//
//        return "helooo ";
//    }
//
//    private int generateInnerQuery(Object value) {
//        System.out.println(value);
//
//        return 1;
//    }
    public void getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {
        StringBuilder query;

        for (MethodConfigDTO method : methodConfigDTOList) {
            generateScript(method, tableNames.get("parent"));
        }

    }

    private String generateScript(Object method, String tableName) throws IllegalAccessException {

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");

        // getting fields in a method
        Field[] fields = method.getClass().getDeclaredFields();

        //Loop fields to get column names
        for (int i = 0; i < fields.length; i++) {
//            if (!fields[i].isAccessible()) {
                fields[i].setAccessible(true);
//            }
            query.append(fields[i].getName());
//            System.out.println(fields[i].getName());
            if (i != fields.length - 1) query.append(" ,");
        }
        query.append(" ) VALUES (");

        // loop to find values
        for (int i = 0; i < fields.length; i++) {
            Object value = fields[i].get(method);
            //System.out.println(value);
            Class<?> fieldType = fields[i].getType();
            if (value == null || fieldType.isPrimitive() || fieldType == String.class || Number.class.isAssignableFrom(fieldType) || fieldType == UUID.class) {
                // Handle primitive types or String or subclasses of Number
                query.append(value + " ,");
                //System.out.println(fields[i].getName() + " " + value + "  " + fields[i].getType() + " " + "true");
            }
//            else if(List.class.isAssignableFrom(value.getClass())){
//
//            } else if(Map.)
            else {
                //System.out.println(fields[i].getName() + " " + value + "  " + fields[i].getType() + " " + "false");
                //query.append("null");
               // System.out.println("key---" + fields[i].getName());

                String table = tableNames.get(fields[i].getName());
                //System.out.println("TableName---" + table);
//                if(List.class.isAssignableFrom(value.getClass())){
//                    List<Object> values = (List) value;
//                    for(Object val: values){
//                        generateScript(val,table);
//                    }

                // Handle both single objects and lists consistently
                List<Object> values = (List<Object>) (value instanceof List ? value : List.of(value));
                for (Object val : values) {
                    generateScript(val, table);
                }


//                generateScript(value, table);
                if (i != fields.length - 1) query.append(" ,");

            }
        }
        query.append(")");
        System.out.println(query.toString());

        return query.toString();

    }

    }

