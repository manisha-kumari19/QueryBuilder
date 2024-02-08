package com.QueryBuilder.service;

import com.QueryBuilder.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {
    // Autowired ObjectMapper for JSON processing
    @Autowired
    public ObjectMapper objectMapper;


    // Map to store table names
    public Map<String, String> tableNames = new HashMap<String, String>();
    // Counters for various elements
    public int methodConfig = 0;
    public int blockConfig = 0;
    public int variable = 0;
    public int statement = 0;
    public int statementDetails = 0;
    public int operators = 0;
    public int expressions = 0;
    public int leftOperands = 0;
    public int rightOperands = 0;
    public int methodArgument = 0;
    public int operand = 0;
    public int methodquery = 0;

    // Constructor with ObjectMapper initialization and tableNames mapping
    public QueryService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // Mapping table names to corresponding keys
        tableNames.put("blockConfig", "config.m_block_config");
        tableNames.put("methodVariablesList", "config.m_method_variables");
        tableNames.put("methodStatementDetailList", "config.m_method_statement_detail");
        tableNames.put("methodStatementList", "config.m_method_statement");
        tableNames.put("expressionConfig", "config.m_expression_config");
        tableNames.put("operand", "config.m_operand_config");
        tableNames.put("operator", "config.m_operators");
        tableNames.put("methodArgumentsConfigList", "config.m_method_arguments_config");
        tableNames.put("methodVariables", "config.m_method_variables");
        tableNames.put("parent", "config.m_method_config");
        tableNames.put("leftOperand", "config.m_operand_config");
        tableNames.put("rightOperand", "config.m_operand_config");
        tableNames.put("methodquery", "config.m_query_config");

    }

    // Method to fix single quotes in a query
    public static String fixQuery(String originalQuery) {
        // Replace single quotes with two single quotes
        String fixedQuery = originalQuery.replace("'", "''");
        return fixedQuery;
    }

    // This method will reset counters for each method
    public void resetCounters() {
        methodConfig = 0;
        blockConfig = 0;
        variable = 0;
        statement = 0;
        statementDetails = 0;
        operators = 0;
        expressions = 0;
        leftOperands = 0;
        rightOperands = 0;
        methodArgument = 0;
        operand = 0;
        methodquery = 0;

    }

    public List<MethodConfigDTO> mapJsonToDTO(String jsonString) throws IOException {
        TypeReference<List<MethodConfigDTO>> typeReference = new TypeReference<List<MethodConfigDTO>>() {
        };
        return objectMapper.readValue(jsonString, typeReference);
    }

    public String getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {
        StringBuilder queries = new StringBuilder();
        StringBuilder updateQueries = new StringBuilder();
        StringBuilder script = new StringBuilder();
        script.append("DO $$ \n DECLARE \n");

        // Iterate through each method in the list
        for (MethodConfigDTO method : methodConfigDTOList) {
            resetCounters();
            generateScriptForMethod(method, queries, updateQueries,null,0);

            // Declare variables for each counter
            for (int i = 1; i <= methodConfig; i++) {
                script.append("v_method_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= blockConfig; i++) {
                script.append("v_block_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= variable; i++) {
                script.append("v_method_variable_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= statement; i++) {
                script.append("v_method_statement_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= statementDetails; i++) {
                script.append("v_method_statement_detail_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= operators; i++) {
                script.append("v_operator_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= expressions; i++) {
                script.append("v_expression_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= leftOperands; i++) {
                script.append("v_left_operand_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= rightOperands; i++) {
                script.append("v_right_operand_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= methodArgument; i++) {
                script.append("v_method_arguments_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= operand; i++) {
                script.append("v_operand_id_" + i + " UUID ;\n");
            }
            for (int i = 1; i <= methodquery; i++) {
                script.append("v_query_id_" + i + " UUID ;\n");
            }
            script.append("v_method_to_be_called UUID;\n");
            script.append("v_return_type_schema_def_id  UUID;\n" );
            script.append("v_method_name VARCHAR;\n");
            script.append("v_name VARCHAR;\n");
            script.append("v_schema_def_id UUID;\n");
            script.append("v_package_name VARCHAR;\n");
            script.append("p_name VARCHAR;\n");
            script.append("  v_package_count INT;\n");
            script.append("v_method_variable_id UUID;\n");
            script.append("\n BEGIN \n");
            script.append(queries);
            script.append(updateQueries);
            script.append("\nCOMMIT; \nEND $$;");
            saveScriptToFile(method.getName(), script.toString());
            script = new StringBuilder("DO $$ \n DECLARE \n");
            updateQueries = new StringBuilder();
        }


        return script.toString();

    }

    //method for script generation of methodConfig
    public void generateScriptForMethod(MethodConfigDTO method, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        methodConfig++;
        String tableName = tableNames.get("parent");

        //for ReturnTypeschemaDefId
        String returnTypeSchemaDefId = method.getReturn_type_schema_def_id();
        String[] parts = returnTypeSchemaDefId.split("\\.");

        String Name = parts[parts.length - 1];

        // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
        String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
        queries.append("v_name := '" + Name + "';\n");
        queries.append("v_package_name := '" + packageName + "';\n");

        queries.append("SELECT id INTO v_return_type_schema_def_id  FROM  config.oas_schema WHERE package_name = v_package_name::ltree AND name = v_name LIMIT 1;");


       //Check if package name exists
        packageName = method.getPackage_name();
        queries.append("v_package_name := '" + packageName + "';\n");
        String packageCheckQuery = "SELECT COUNT(*) FROM config.package WHERE name = $1 ;" + "\n"; // yaha doubt hai



//        queries.append("  EXECUTE '" + packageCheckQuery + "' INTO v_package_count USING v_package_name ::ltree;\n");
//        queries.append("  IF v_package_count = 0 THEN\n");
//        queries.append("    -- Package name does not exist, insert into 'package' table first\n");
//        queries.append("    INSERT INTO config.package(name) VALUES (v_package_name::ltree);\n");
//        queries.append("  END IF;\n");
//        queries.append("    -- Package name exists, proceed with the insert\n");

        queries.append("    INSERT INTO " + tableName + "(id, name, is_archive, return_type_schema_def_id, block_id, package_name, is_using_multiple_db, type) VALUES (" +
                "uuid_generate_v4(), '" + method.getName() + "', '" + method.is_archive() + "', v_return_type_schema_def_id , " +
                "NULL,'"+method.getPackage_name()  + "', '" + method.getIs_using_multiple_db() + "', '" + method.getType() + "') RETURNING id INTO v_method_id_" + methodConfig + ";\n");



        if (method.getMethodVariablesList() != null && !method.getMethodVariablesList().isEmpty()) {
            for (MethodVariableDTO variable : method.getMethodVariablesList()) {
                generateScriptForMethodVariables(variable, queries, updateQueries, tableName, methodConfig);
            }

        }
        if (method.getBlockConfig() != null) {
            generateScriptForBlock(method.getBlockConfig(), queries, updateQueries, tableName, methodConfig);
        }

    }

    //method for script generation of blockConfig
    public void generateScriptForBlock(BlockConfigDTO block, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        blockConfig++;
        int blockConfigCounter = blockConfig;
        String tableName = tableNames.get("blockConfig");
        queries.append("INSERT INTO " + tableName + " (id,parent_block_id) VALUES (" + "uuid_generate_v4()" + "," + block.getParent_block_id() + " ) RETURNING id INTO v_block_id_" + blockConfigCounter + ";\n");
        if (parent.equals("config.m_method_config")) {
            updateQueries.append(" UPDATE config.m_method_config SET block_id = v_block_id_" + blockConfigCounter + " WHERE id = v_method_id_" + parentCounter + ";\n");
        }
        if (parent.equals("config.m_method_statement_detail")) {
            updateQueries.append(" UPDATE config.m_method_statement_detail SET block_id = v_block_id_" + blockConfigCounter + " WHERE id = v_method_statement_detail_id_" + parentCounter + ";\n");
        }


        if (block.getMethodStatementList() != null && !block.getMethodStatementList().isEmpty()) {
            for (MethodStatementDTO statement : block.getMethodStatementList()) {
                generateScriptForStatement(statement, queries, updateQueries, tableName, blockConfigCounter);
            }
        }
    }

    //method for script generation of methodStatement config
    public void generateScriptForStatement(MethodStatementDTO statements, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        statement++;
        int statementCounter = statement;
        String tableName = tableNames.get("methodStatementList");
        queries.append("INSERT INTO " + tableName + " (id,type,sequence,block_id) VALUES (" + "uuid_generate_v4()," + "'" + statements.getType() + "','" + statements.getSequence() + "'," + "v_block_id_" + parentCounter + ")" + "RETURNING id INTO v_method_statement_id_" + statementCounter + ";\n");
        if (statements.getMethodStatementDetailList() != null && !statements.getMethodStatementDetailList().isEmpty()) {
            for (MethodStatementDetailDTO statementDetail : statements.getMethodStatementDetailList()) {

                generateScriptForStatementDetail(statementDetail, queries, updateQueries, tableName, statementCounter);

            }
        }
    }


    //method for script generation of statementDetail Config
    public void generateScriptForStatementDetail(MethodStatementDetailDTO statementDetail, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        statementDetails++;
        int statementDetailCounter = statementDetails;
        int blockConfigCounter = blockConfig;
        int expressionCounter = expressions;
        String tableName = tableNames.get("methodStatementDetailList");

        if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() != null) {
            expressionCounter++;
            blockConfigCounter++;
            generateScriptForBlock(statementDetail.getBlockConfig(), queries, updateQueries, tableName, statementDetailCounter);
            generateScriptForExpression(statementDetail.getExpressionConfig(), queries, updateQueries, tableName, statementDetailCounter);
            queries.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + parentCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressionCounter + ",v_block_id_" + blockConfigCounter + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");
        } else if (statementDetail.getBlockConfig() == null && statementDetail.getExpressionConfig() != null) {
            expressionCounter++;
            generateScriptForExpression(statementDetail.getExpressionConfig(), queries, updateQueries, tableName, statementDetailCounter);
            queries.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + parentCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressionCounter + ",NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

        } else if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() == null) {
            blockConfigCounter++;
            generateScriptForBlock(statementDetail.getBlockConfig(), queries, updateQueries, tableName, statementDetailCounter);
            queries.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + parentCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,v_block_id_" + blockConfigCounter + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

        } else {
            queries.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + parentCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,NULL,NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

        }
    }

    //method for script generation of expressionConfig
    public void generateScriptForExpression(ExpressionConfigDTO expression, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        expressions++;
        int expressionCounter = expressions;
        int leftOperandCounter = leftOperands;
        int rightOperandCounter = rightOperands;
        int operatorCounter = operators;
        String tableName = tableNames.get("expressionConfig");


        if (expression.getOperator() != null) {
            operatorCounter++;
            if (expression.getLeftOperand() != null && expression.getRightOperand() != null) {
                leftOperandCounter++;
                rightOperandCounter++;
                generateScriptForLeftOperand(expression.getLeftOperand(), queries, updateQueries, tableName, expressionCounter);
                queries.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",v_right_operand_id_" + rightOperandCounter + ",v_operator_id_" + operatorCounter + ") RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");
                generateScriptForOperator(expression.getOperator(), queries, updateQueries, tableName, expressionCounter);
                generateScriptForRightOperand(expression.getRightOperand(), queries, updateQueries, tableName, expressionCounter);

                if (parent.equals("right")) {

                    updateQueries.append("UPDATE config.m_operand_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_right_operand_id_" + parentCounter + " ;\n");
                } else if (parent.equals("left")) {
                    updateQueries.append("UPDATE config.m_operand_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_left_operand_id_" + parentCounter + " ;\n");

                } else if (parent.equals("config.m_method_statement_detail")) {
                    updateQueries.append("UPDATE config.m_method_statement_detail SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_statement_detail_id_" + (parentCounter) + " ;\n");
                } else if (parent.equals("config.m_method_arguments_config")) {
                    updateQueries.append("UPDATE config.m_method_arguments_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_arguments_id_" + parentCounter + " ;\n");
                }

            } else if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                leftOperandCounter++;
                generateScriptForLeftOperand(expression.getLeftOperand(), queries, updateQueries, tableName, expressionCounter);
                queries.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",NULL" + ",v_operator_id_" + operatorCounter + ") RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");
                generateScriptForOperator(expression.getOperator(), queries, updateQueries, tableName, expressionCounter);

                if (parent.equals("right")) {
                    updateQueries.append("UPDATE config.m_operand_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_right_operand_id_" + parentCounter + " ;\n");
                } else if (parent.equals("config.m_method_statement_detail")) {
                    updateQueries.append("UPDATE config.m_method_statement_detail SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_statement_detail_id_" + (parentCounter) + " ;\n");
                } else if (parent.equals("config.m_method_arguments_config")) {
                    updateQueries.append("UPDATE config.m_method_arguments_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_arguments_id_" + parentCounter + " ;\n");
                }

            }
        } else if (expression.getOperator() == null) {
            if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                leftOperandCounter++;
                generateScriptForLeftOperand(expression.getLeftOperand(), queries, updateQueries, tableName, expressionCounter);
                queries.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",NULL" + " , NULL) RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");
                if (parent.equals("right")) {
                    updateQueries.append("UPDATE config.m_operand_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_right_operand_id_" + parentCounter + " ;\n");
                } else if (parent.equals("config.m_method_statement_detail")) {
                    updateQueries.append("UPDATE config.m_method_statement_detail SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_statement_detail_id_" + (parentCounter) + " ;\n");
                } else if (parent.equals("config.m_method_arguments_config")) {
                    updateQueries.append("UPDATE config.m_method_arguments_config SET expression_id=v_expression_id_" + expressionCounter + " WHERE id=v_method_arguments_id_" + parentCounter + " ;\n");
                }
            }
        }
    }

    //method for script generation of OperandConfig
    public void generateScriptForLeftOperand(LeftOperandConfigDTO leftOperand, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        leftOperands++;
        int leftOperandCounter = leftOperands;
        int expressionCounter = expressions;
        String tableName = tableNames.get("operand");

        if (leftOperand.getMethodToBeCalled() != null) {
            String methodToBeCalled = leftOperand.getMethodToBeCalled();
            String[] parts = methodToBeCalled.split("\\.");

            // v_method_name will be the last part after the last dot
            String methodName = parts[parts.length - 1];

            // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
            String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
            queries.append("v_method_name := '" + methodName + "';\n");
            queries.append("v_package_name := '" + packageName + "';\n");

            // Set v_method_to_be_called to the desired method_id
            queries.append("SELECT id INTO v_method_to_be_called FROM config.m_method_config WHERE package_name = v_package_name::ltree AND name = v_method_name LIMIT 1;");


            // Insert into config.m_operand_config
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) " + "VALUES (uuid_generate_v4(), NULL, NULL, v_method_to_be_called, NULL, '" + leftOperand.getType() + "', NULL) " + "RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
            // Iterates over list of MethodArguments Configuration
            List<MethodArgumentsConfigDTO> methodArgumentsList = leftOperand.getMethodArgumentsConfigList();
            if (methodArgumentsList != null && !methodArgumentsList.isEmpty()) {
                for (MethodArgumentsConfigDTO methodArguments : methodArgumentsList) {
                    generateScriptForMethodArguments(methodArguments, queries, updateQueries, "left", leftOperandCounter);
                }
            }
        } else if (leftOperand.getExpressionConfig() != null) {
            expressionCounter++;
            generateScriptForExpression(leftOperand.getExpressionConfig(), queries, updateQueries, "left", leftOperandCounter);
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressionCounter + ",'" + leftOperand.getType() + "'," + "NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
        } else if (leftOperand.getPathToObject() != null) {
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + leftOperand.getPathToObject() + "',NULL,NULL,NULL,' " + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
        } else if (leftOperand.getLiteral() != null) {
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + leftOperand.getLiteral() + "',NULL,NULL, '" + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
        } else if (leftOperand.getQueryConfig() != null) {
            generateScriptForQuery(leftOperand.getQueryConfig(), queries, updateQueries, "left", leftOperandCounter);
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,NULL ,NULL,NULL,'" + leftOperand.getType() + "',v_query_id_" + methodquery + ") RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
        }
    }

    public void generateScriptForRightOperand(RightOperandConfigDTO rightOperand, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        rightOperands++;
        int rightOperandsCounter = rightOperands;
        int expressionCounter = expressions;
        int queryCounter = methodquery;
        String tableName = tableNames.get("operand");

        if (rightOperand.getMethodToBeCalled() != null) {
            String methodToBeCalled = rightOperand.getMethodToBeCalled();
            String[] parts = methodToBeCalled.split("\\.");

            // v_method_name will be the last part after the last dot
            String methodName = parts[parts.length - 1];

            // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
            String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
            queries.append("v_method_name := '" + methodName + "';\n");
            queries.append("v_package_name := '" + packageName + "';\n");

            // Set v_method_to_be_called to the desired method_id
            queries.append("SELECT id INTO v_method_to_be_called FROM config.m_method_config WHERE package_name = v_package_name::ltree AND name = v_method_name LIMIT 1;");


            // Insert into config.m_operand_config
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) " + "VALUES (uuid_generate_v4(), NULL, NULL, v_method_to_be_called, NULL, '" + rightOperand.getType() + "', NULL) " + "RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            updateQueries.append("UPDATE config.m_expression_config SET right_operand_id = v_right_operand_id_" + rightOperandsCounter + " WHERE id=v_expression_id_" + parentCounter + ";\n");



            List<MethodArgumentsConfigDTO> methodArgumentsList = rightOperand.getMethodArgumentsConfigList();
            if (methodArgumentsList != null && !methodArgumentsList.isEmpty()) {
                for (MethodArgumentsConfigDTO methodArguments : methodArgumentsList) {
                    generateScriptForMethodArguments(methodArguments, queries, updateQueries, "right", rightOperandsCounter);
                }
            }


        } else if (rightOperand.getExpressionConfig() != null) {
            expressionCounter++;
            generateScriptForExpression(rightOperand.getExpressionConfig(), queries, updateQueries, "right", rightOperandsCounter);
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressionCounter + ",'" + rightOperand.getType() + "'," + "NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            updateQueries.append("UPDATE config.m_expression_config SET right_operand_id = v_right_operand_id_" + rightOperandsCounter + " WHERE id=v_expression_id_" + parentCounter + ";\n");
        } else if (rightOperand.getPathToObject() != null) {
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + rightOperand.getPathToObject() + "',NULL,NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            updateQueries.append("UPDATE config.m_expression_config SET right_operand_id = v_right_operand_id_" + rightOperandsCounter + " WHERE id=v_expression_id_" + parentCounter + ";\n");
        } else if (rightOperand.getLiteral() != null) {
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + rightOperand.getLiteral() + "',NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            updateQueries.append("UPDATE config.m_expression_config SET right_operand_id = v_right_operand_id_" + rightOperandsCounter + " WHERE id=v_expression_id_" + parentCounter + ";\n");
        } else if (rightOperand.getQueryConfig() != null) {
            queryCounter++;
            generateScriptForQuery(rightOperand.getQueryConfig(), queries, updateQueries, "right", rightOperandsCounter);
            queries.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,NULL ,NULL,NULL,'" + "'" + rightOperand.getType() + "'" + "',v_query_id_" + queryCounter + ") RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");

        }
    }

    //method for script generation of operatorConfig
    public void generateScriptForOperator(OperatorDTO operator, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        operators++;
        int operatorCounter = operators;
        String tableName = tableNames.get("operator");
        queries.append("INSERT INTO " + tableName + "(id,is_archive,name,type) VALUES(" + "uuid_generate_v4(),'" + operator.is_archive() + "','" + operator.getName() + "','" + operator.getType() + "') ON CONFLICT (id)\n" + "DO NOTHING  RETURNING ID INTO v_operator_id_" + operatorCounter + ";\n");
        if (parent.equals("config.m_expression_config")) {
            updateQueries.append(" UPDATE config.m_expression_config SET operator_id = v_operator_id_" + operatorCounter + " WHERE id = v_expression_id_" + parentCounter + ";\n");
        }
    }

    //method for script generation of methodArgumentsConfig
    public void generateScriptForMethodArguments(MethodArgumentsConfigDTO methodArguments, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        methodArgument++;
        int methodArgumentsCounter = methodArgument;
        int expressionCounter = expressions;
        String tableName = tableNames.get("methodArgumentsConfigList");
        String variableName = methodArguments.getMethod_variable_id();
        queries.append("SELECT id INTO v_method_variable_id FROM config.m_method_variables WHERE method_id=v_method_to_be_called AND variable_name=" + "'" + variableName + "'" + " AND type='PARAM' ;\n ");
        if (methodArguments.getExpressionConfig() != null) {
            expressionCounter++;
            generateScriptForExpression(methodArguments.getExpressionConfig(), queries, updateQueries, tableName, methodArgumentsCounter);

            if (parent.equalsIgnoreCase("left")) {
                queries.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_" + parentCounter + ",v_method_variable_id" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressionCounter + ")RETURNING ID INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
            } else if (parent.equalsIgnoreCase("right")) {
                queries.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_" + parentCounter + ",v_method_variable_id" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressionCounter + ")RETURNING ID INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
            }
        } else {
            if (parent.equalsIgnoreCase("left")) {
                queries.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_" + parentCounter + ",v_method_variable_id" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
            } else if (parent.equalsIgnoreCase("right")) {
                queries.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_" + parentCounter + ",v_method_variable_id" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
            }
        }


    }

    //method for generation of methodVariables
    public void generateScriptForMethodVariables(MethodVariableDTO variables, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        variable++;
        int variableCounter = variable;
        String tableName = tableNames.get("methodVariablesList");
        //for SchemaDefId;
        String SchemaDefId = variables.getSchema_def_id();
        String[] parts = SchemaDefId.split("\\.");

        String name = parts[parts.length - 1];

        // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
        String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
        queries.append("v_name := '" + name + "';\n");
        queries.append("v_package_name := '" + packageName + "';\n");

        queries.append("SELECT id INTO v_schema_def_id  FROM  config.oas_schema WHERE package_name = v_package_name::ltree AND name = v_name LIMIT 1;");



        queries.append("INSERT INTO " + tableName + "(id,variable_name,type,schema_def_Id,method_id) VALUES(" + "uuid_generate_v4(),'" + variables.getVariable_name() + "','" + variables.getType() + "', v_schema_def_id ," + "v_method_id_" + parentCounter + ") RETURNING ID INTO v_method_variable_id_" + variableCounter + ";\n");

    }

    //method for script generation of QueryConfig
    public void generateScriptForQuery(QueryConfigDTO query, StringBuilder queries, StringBuilder updateQueries, String parent, int parentCounter) {
        methodquery++;
        int queryCounter = methodquery;
        String tableName = tableNames.get("methodquery");
        String fixedQuery = fixQuery(query.getQuery());
        queries.append("INSERT INTO " + tableName + "(id, query,database_config_id) VALUES( uuid_generate_v4(),'" + fixedQuery + "','" + query.getDatabase_config_id() + "')RETURNING id INTO v_query_id_" + queryCounter + ";\n");
    }

    // Method to save the generated script to a file
    private void saveScriptToFile(String methodName, String script) {
        String username = System.getenv("USER");
        Path directoryPath = Paths.get("/home", username, "/bm_scripts", "/method_config");
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            e.printStackTrace();

        }

        // Construct the file path
        Path filePath = Paths.get(directoryPath.toString(), methodName + ".sql");


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
            writer.write(script);
            System.out.println("Script saved to file: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
