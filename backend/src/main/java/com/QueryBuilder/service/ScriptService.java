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
public class ScriptService {

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
    public ScriptService(ObjectMapper objectMapper) {
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

    // Method to map JSON to DTO
    public List<MethodConfigDTO> mapJsonToDTO(String jsonString) throws IOException {
        TypeReference<List<MethodConfigDTO>> typeReference = new TypeReference<List<MethodConfigDTO>>() {
        };
        return objectMapper.readValue(jsonString, typeReference);
    }

    // Method to generate script for all methods and save to files
    public String getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {
        String queries = "";
        StringBuilder script = new StringBuilder();
        script.append("DO $$ \n DECLARE \n");

        // Iterate through each method in the list
        for (MethodConfigDTO method : methodConfigDTOList) {
            resetCounters();
            queries = generateScript(method, null);

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
            script.append("v_method_name VARCHAR;\n");
            script.append("v_package_name VARCHAR;\n");
            script.append("\n BEGIN \n");
            script.append(queries);
            script.append("\nCOMMIT; \nEND $$;");
            saveScriptToFile(method.getName(), script.toString());
            script = new StringBuilder("DO $$ \n DECLARE \n");
        }


        return script.toString();

    }

    // Method to generate script for a given object
    public String generateScript(Object object, String parent) {
        StringBuilder query = new StringBuilder();


        // Handling different DTO types

        //method config
        if (object instanceof MethodConfigDTO method) {
            methodConfig++;
            String tableName = tableNames.get("parent");
            query.append("INSERT INTO " + tableName + "(id,name,is_archive,return_type_schema_def_id,block_id,package_name,is_using_multiple_db,type) VALUES (" + "uuid_generate_v4()" + ",'" + method.getName() + "','" + method.is_archive() + "','" + method.getReturn_type_schema_def_id() + "'," + "NULL" + ",'" + method.getPackage_name() + "','" + method.getIs_using_multiple_db() + "','" + method.getType() + "') RETURNING id INTO v_method_id_" + methodConfig + ";\n");
            if (method.getMethodVariablesList() != null && !method.getMethodVariablesList().isEmpty()) {
                for (MethodVariableDTO variable : method.getMethodVariablesList()) {
                    query.append(generateScript(variable, null));  // Append the query for each method variable
                }

            }
            if (method.getBlockConfig() != null) {
                query.append(generateScript(method.getBlockConfig(), tableName));
            }
        }

        //block_config
        else if (object instanceof BlockConfigDTO block) {

                blockConfig++;
                int blockConfigCounter=(blockConfig == 0) ? 1 : blockConfig ;
                int statementDetailsCounter=(statement == 0)? 1 : statement;
                String tableName = tableNames.get("blockConfig");
                query.append("INSERT INTO " + tableName + " (id,parent_block_id) VALUES (" + "uuid_generate_v4()" + "," + block.getParent_block_id() + " ) RETURNING id INTO v_block_id_" + blockConfigCounter + ";\n");
                if (parent.equals("config.m_method_config")) {
                    query.append(" UPDATE config.m_method_config SET block_id = v_block_id_" + blockConfigCounter + " WHERE id = v_method_id_" + methodConfig + ";\n");
                }


                if (block.getMethodStatementList() != null && !block.getMethodStatementList().isEmpty()) {
                    for (MethodStatementDTO statement : block.getMethodStatementList()) {
                    query.append(generateScript(statement, null));
                }
            }
            if (parent.equals("config.m_method_statement_detail")) {
                query.append(" UPDATE config.m_method_statement_detail SET block_id = v_block_id_" + blockConfigCounter + " WHERE id = v_method_statement_detail_id_" + statementDetailsCounter + ";\n");
            }

        }

        //method statement
        else if (object instanceof MethodStatementDTO statements) {
            statement++;
            int statementCounter=(statement == 0)? 1 : statement;
            int blockConfigCounter=(blockConfig == 0) ? 1 : blockConfig ;;
            String tableName = tableNames.get("methodStatementList");
            System.out.println("-----Statement Counter : "+statementCounter);
            query.append("INSERT INTO " + tableName + " (id,type,sequence,block_id) VALUES (" + "uuid_generate_v4()," + "'" + statements.getType() + "','" + statements.getSequence() + "'," + "v_block_id_" + blockConfigCounter + ")" + "RETURNING id INTO v_method_statement_id_" + statementCounter + ";\n");
            if (statements.getMethodStatementDetailList() != null && !statements.getMethodStatementDetailList().isEmpty()) {
                for (MethodStatementDetailDTO statementDetail : statements.getMethodStatementDetailList()) {
                    query.append(generateScript(statementDetail, null));
                }
            }
        }

        //method_statement_detail
        else if (object instanceof MethodStatementDetailDTO statementDetail) {
            statementDetails++;
            int statementDetailCounter=(statementDetails == 0)? 1 : statementDetails ;
            int statementCounter=(statement == 0)? 1  : statement;;
            int blockConfigCounter=(blockConfig == 0) ? 1 : blockConfig ;;
            int expressionCounter=(expressions==0)? 1:expressions;
            String tableName = tableNames.get("methodStatementDetailList");

            if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() != null) {
                query.append(generateScript(statementDetail.getBlockConfig(), tableName));
                query.append(generateScript(statementDetail.getExpressionConfig(), tableName));
                System.out.println("-----StatementDetail Counter : "+statementCounter);
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statementCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressionCounter + ",v_block_id_" + blockConfigCounter + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");
            } else if (statementDetail.getBlockConfig() == null && statementDetail.getExpressionConfig() != null) {
                query.append(generateScript(statementDetail.getExpressionConfig(), tableName));
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statementCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressionCounter + ",NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

            } else if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() == null) {
                query.append(generateScript(statementDetail.getBlockConfig(), tableName));
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statementCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,v_block_id_" + blockConfigCounter + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

            } else {
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statementCounter + "," + statementDetail.getSequence() + ",'" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,NULL,NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetailCounter + ";\n");

            }


        }

        //expression_config
        else if (object instanceof ExpressionConfigDTO expression) {
            expressions++;
            int expressionCounter =(expressions==0)? 1 :expressions;
            int leftOperandCounter=(leftOperands == 0)? 1 :leftOperands;
            int rightOperandCounter=(rightOperands == 0)? 1 :rightOperands;;
            int operatorCounter=(operators == 0)? 1:operators;;
            String tableName = tableNames.get("expressionConfig");


            if (expression.getOperator() != null) {
                if (expression.getLeftOperand() != null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append(generateScript(expression.getRightOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",v_right_operand_id_" + rightOperandCounter + ",v_operator_id_" + operatorCounter + ") RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");
                } else if (expression.getLeftOperand() == null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getRightOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() , NULL" + ",v_right_operand_id_" + rightOperandCounter + ",v_operator_id_" + operatorCounter + ") RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");

                } else if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",NULL" + ",v_operator_id_" + operatorCounter + ") RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");


                }
            } else if (expression.getOperator() == null) {
                if (expression.getLeftOperand() == null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getRightOperand(), tableName));

                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4(), NULL  " + ",v_right_operand_id_" + rightOperandCounter + ",NULL ) RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");

                } else if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperandCounter + ",NULL" + " , NULL) RETURNING id INTO v_expression_id_" + expressionCounter + ";\n");

                }
            }
        }

        // left operand_config
        else if (object instanceof LeftOperandConfigDTO leftOperand) {
            leftOperands++;
            int leftOperandCounter=(leftOperands == 0)? 1:leftOperands;
            int expressionCounter=(expressions == 0)? 1:expressions;
            int methodQueryCounter=(methodquery == 0)? 1:methodquery;
            String tableName = tableNames.get("operand");

            if (leftOperand.getMethodToBeCalled() != null) {
                String methodToBeCalled = leftOperand.getMethodToBeCalled();
                String[] parts = methodToBeCalled.split("\\.");

                // v_method_name will be the last part after the last dot
                String methodName = parts[parts.length - 1];

                // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
                String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
                query.append("v_method_name := '" + methodName + "';\n");
                query.append("v_package_name := '" + packageName + "';\n");

                // Set v_method_to_be_called to the desired method_id
                query.append("SELECT id INTO v_method_to_be_called FROM config.m_method_config WHERE package_name = v_package_name::ltree AND name = v_method_name LIMIT 1;");


                // Insert into config.m_operand_config
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) " + "VALUES (uuid_generate_v4(), NULL, NULL, v_method_to_be_called, NULL, '" + leftOperand.getType() + "', NULL) " + "RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");

                // Iterates over list of MethodArguments Configuration
                List<MethodArgumentsConfigDTO> methodArgumentsList = leftOperand.getMethodArgumentsConfigList();
                if (methodArgumentsList != null) {
                    for (MethodArgumentsConfigDTO methodArguments : methodArgumentsList) {
                        query.append(generateScript(methodArguments, "left"));
                    }
                }
            } else if (leftOperand.getExpressionConfig() != null) {
                query.append(generateScript(leftOperand.getExpressionConfig(), "left"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressionCounter + ",'" + leftOperand.getType() + "'," + "NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
            } else if (leftOperand.getPathToObject() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + leftOperand.getPathToObject() + "',NULL,NULL,NULL,' " + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");

            } else if (leftOperand.getLiteral() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + leftOperand.getLiteral() + "',NULL,NULL, '" + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");
            } else if (leftOperand.getQueryConfig() != null) {
                query.append(generateScript(leftOperand.getQueryConfig(), "left"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,NULL ,NULL,NULL,'" + leftOperand.getType() + "',v_query_id_" + methodQueryCounter + ") RETURNING id INTO v_left_operand_id_" + leftOperandCounter + ";\n");

            }
        }


        //right operand
        else if (object instanceof RightOperandConfigDTO rightOperand) {
            rightOperands++;
            int rightOperandsCounter=(rightOperands == 0)? 1:rightOperands;;
            int expressionCounter=(expressions == 0)? 1:expressions;
            int methodQueryCounter=(leftOperands == 0)? 1:leftOperands;;
            String tableName = tableNames.get("operand");

            if (rightOperand.getMethodToBeCalled() != null) {

                String methodToBeCalled = rightOperand.getMethodToBeCalled();

                String[] parts = methodToBeCalled.split("\\.");

                // v_method_name will be the last part after the last dot
                String methodName = parts[parts.length - 1];

                // v_package_name will be all the parts before the last dot, excluding the last part (v_method_name)
                String packageName = String.join(".", Arrays.copyOf(parts, parts.length - 1));
                query.append("v_method_name := '" + methodName + "';\n");
                query.append("v_package_name := '" + packageName + "';\n");

                // Set v_method_to_be_called to the desired method_id
                query.append("SELECT id INTO v_method_to_be_called FROM config.m_method_config WHERE package_name = v_package_name::ltree AND name = v_method_name LIMIT 1;");


                // Insert into config.m_operand_config
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) " +
                        "VALUES (uuid_generate_v4(), NULL, NULL, v_method_to_be_called, NULL, '" + rightOperand.getType() + "', NULL) " +
                        "RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");

                for (MethodArgumentsConfigDTO methodArguments : rightOperand.getMethodArgumentsConfigList()) {
                    query.append(generateScript(methodArguments, "right"));
                }
            } else if (rightOperand.getExpressionConfig() != null) {
                query.append(generateScript(rightOperand.getExpressionConfig(), "right"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressionCounter + ",'" + rightOperand.getType() + "'," + "NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            } else if (rightOperand.getPathToObject() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + rightOperand.getPathToObject() + "',NULL,NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");

            } else if (rightOperand.getLiteral() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + rightOperand.getLiteral() + "',NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");
            } else if (rightOperand.getQueryConfig() != null) {
                query.append(generateScript(rightOperand.getQueryConfig(), "right"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,NULL ,NULL,NULL,'" + "'" + rightOperand.getType() + "'" + "',v_query_id_" + methodQueryCounter + ") RETURNING id INTO v_right_operand_id_" + rightOperandsCounter + ";\n");

            }


        }

        //operator_config
        else if (object instanceof OperatorDTO operator) {
            operators++;
            int operatorCounter=(operators == 0)? 1:operators;;
            int expressionCounter=(expressions == 0)? 1:expressions;
            String tableName = tableNames.get("operator");
            query.append("INSERT INTO " + tableName + "(id,is_archive,name,type) VALUES(" + "uuid_generate_v4(),'" + operator.is_archive() + "','" + operator.getName() + "','" + operator.getType() + "') ON CONFLICT (id)\n" + "DO NOTHING  RETURNING ID INTO v_operator_id_" + operatorCounter + ";\n");
            if (parent.equals("config.m_expression_config")) {
                query.append(" UPDATE config.m_expression_config SET operator_id = v_operator_id_" + operatorCounter + " WHERE id = v_expression_id_" + expressionCounter + ";\n");
            }
        }
//
        //method_Arguments_config
        else if (object instanceof MethodArgumentsConfigDTO methodArguments) {
            methodArgument++;
            int methodArgumentsCounter=(methodArgument == 0)? 1:methodArgument;
            int leftOperandCounter=(leftOperands == 0)? 1:leftOperands;
            int rightOperandCounter=(rightOperands == 0)? 1:rightOperands;
            int expressionCounter=(expressions == 0)? 1:expressions;
            String tableName = tableNames.get("methodArgumentsConfigList");

            if (methodArguments.getExpressionConfig() != null) {
                query.append(generateScript(methodArguments.getExpressionConfig(), tableName));
                if (parent.equalsIgnoreCase("left")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_" + leftOperandCounter + ",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressionCounter + ")RETURNING ID INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
                } else if (parent.equalsIgnoreCase("right")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_" + rightOperandCounter + ",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressionCounter + ")RETURNING ID INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
                }
            } else {
                if (parent.equalsIgnoreCase("left")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_" + leftOperandCounter + ",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
                } else if (parent.equalsIgnoreCase("right")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_" + rightOperandCounter + ",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgumentsCounter + ";\n");
                }
            }


        }

        //method_variable
        else if (object instanceof MethodVariableDTO methodVariable) {
            variable++;
            String tableName = tableNames.get("methodVariablesList");
            query.append("INSERT INTO " + tableName + "(id,variable_name,type,schema_def_Id,method_id) VALUES(" + "uuid_generate_v4(),'" + methodVariable.getVariable_name() + "','" + methodVariable.getType() + "','" + methodVariable.getSchema_def_id() + "'," + "v_method_id_" + methodConfig + ") RETURNING ID INTO v_method_variable_id_" + variable + ";\n");

        } else if (object instanceof QueryConfigDTO queryConfig) {
            methodquery++;
            String tableName = tableNames.get("methodquery");
            String fixedQuery = fixQuery(queryConfig.getQuery());
            query.append("INSERT INTO " + tableName + "(id, query,database_config_id) VALUES( uuid_generate_v4(),'" + fixedQuery + "','" + queryConfig.getDatabase_config_id() + "')RETURNING id INTO v_query_id_" + methodquery + ";\n");
        }
        return query.toString();
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