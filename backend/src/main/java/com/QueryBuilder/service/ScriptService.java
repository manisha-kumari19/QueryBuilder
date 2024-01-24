package com.QueryBuilder.service;
import com.QueryBuilder.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ScriptService {

    @Autowired
    public ObjectMapper objectMapper;

    public Map<String, String> tableNames = new HashMap<String, String>();

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


    public ScriptService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        tableNames.put("methodquery","Config.m_query_config");

    }


    public List<MethodConfigDTO> mapJsonToDTO(String jsonString) throws IOException {
        TypeReference<List<MethodConfigDTO>> typeReference = new TypeReference<List<MethodConfigDTO>>() {
        };
        return objectMapper.readValue(jsonString, typeReference);
    }

    public String getAllMethods(List<MethodConfigDTO> methodConfigDTOList) throws IllegalAccessException {

        String queries = "";
        for (MethodConfigDTO method : methodConfigDTOList) {
            queries = generateScript(method, null);
        }

        StringBuilder script = new StringBuilder();
        script.append("DO $$ \n DECLARE \n");


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


        script.append("\n BEGIN \n");
        script.append(queries);

        script.append("\nCOMMIT; \nEND $$;");
        return script.toString();

    }

    public String generateScript(Object object, String parent) {
        StringBuilder query = new StringBuilder();

        //method config
        if (object instanceof MethodConfigDTO) {
            methodConfig++;
            MethodConfigDTO method = (MethodConfigDTO) object;
            String tableName = tableNames.get("parent");
            String packageValue = null;
            if (method.getPackage_name() != null) {
                packageValue = method.getPackage_name().getValue();
            }

            query.append("INSERT INTO " + tableName + "(id,name,is_archive,return_type_schema_def_id,block_id,package_name,type) VALUES (" + "uuid_generate_v4()" + ",'" +
                    method.getName() + "','" + method.is_archive() + "','" + method.getReturn_type_schema_def_id() + "'," + "NULL" + ",'" + packageValue + "','" + method.getType() + "') RETURNING id INTO v_method_id_" + methodConfig + ";\n"
            );
            if (!method.getMethodVariablesList().isEmpty()) {
                for (MethodVariableDTO variable : method.getMethodVariablesList()) {
                    query.append(generateScript(variable, null));  // Append the query for each method variable
                }
                if (method.getBlockConfig() != null) {
                    query.append(generateScript(method.getBlockConfig(), tableName));
                }
            }
        }

        //block_config
        else if (object instanceof BlockConfigDTO) {
            blockConfig++;
            BlockConfigDTO block = (BlockConfigDTO) object;
            String tableName = tableNames.get("blockConfig");
            query.append("INSERT INTO " + tableName + " (id,parent_block_id) VALUES (" + "uuid_generate_v4()" + "," + block.getParent_block_id() + " ) RETURNING id INTO v_block_id_" + blockConfig + ";\n");
            if (parent.equals("config.m_method_config")) {
                query.append(" UPDATE config.m_method_config SET block_id = v_block_id_" + blockConfig + " WHERE id = v_method_id_" + methodConfig + ";\n");
            }


            if (!block.getMethodStatementList().isEmpty()) {
                for (MethodStatementDTO statement : block.getMethodStatementList()) {
                    query.append(generateScript(statement, null));
                }
            }
            if (parent.equals("config.m_method_statement_detail")) {
                query.append(" UPDATE config.m_method_statement_detail SET block_id = v_block_id_" + blockConfig + " WHERE id = v_method_statement_detail_id_" + statementDetails + ";\n");
            }

        }

//        //method statement
        else if (object instanceof MethodStatementDTO) {
            statement++;
            MethodStatementDTO statements = (MethodStatementDTO) object;
            String tableName = tableNames.get("methodStatementList");
            query.append("INSERT INTO " + tableName + " (id,type,sequence,block_id) VALUES (" + "uuid_generate_v4()," + "'" + statements.getType() + "','" + statements.getSequence() + "'," + "v_block_id_" + blockConfig + ")" + "RETURNING id INTO v_method_statement_id_" + statement + ";\n");
            if (!statements.getMethodStatementDetailList().isEmpty()) {
                for (MethodStatementDetailDTO statementDetail : statements.getMethodStatementDetailList()) {
                    query.append(generateScript(statementDetail, null));
                }
            }


        }

        //method_statement_detail
        else if (object instanceof MethodStatementDetailDTO) {
            statementDetails++;
            MethodStatementDetailDTO statementDetail = (MethodStatementDetailDTO) object;
            String tableName = tableNames.get("methodStatementDetailList");

            if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() != null) {
                query.append(generateScript(statementDetail.getBlockConfig(), tableName));
                query.append(generateScript(statementDetail.getExpressionConfig(), tableName));
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statement + ",'" + statementDetail.getSequence() + "','" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressions + ",v_block_id_" + blockConfig + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetails + ";\n");
            } else if (statementDetail.getBlockConfig() == null && statementDetail.getExpressionConfig() != null) {
                query.append(generateScript(statementDetail.getExpressionConfig(), tableName));
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statement + ",'" + statementDetail.getSequence() + "','" + statementDetail.getMethod_statement_expression_type() + "'," + " v_expression_id_" + expressions + ",NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetails + ";\n");

            } else if (statementDetail.getBlockConfig() != null && statementDetail.getExpressionConfig() == null) {
                query.append(generateScript(statementDetail.getBlockConfig(), tableName));
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statement + ",'" + statementDetail.getSequence() + "','" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,v_block_id_" + blockConfig + ") RETURNING id INTO v_method_statement_detail_id_" + statementDetails + ";\n");

            } else {
                query.append("INSERT INTO " + tableName + " (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_" + statement + ",'" + statementDetail.getSequence() + "','" + statementDetail.getMethod_statement_expression_type() + "'," + "NULL,NULL,NULL) RETURNING id INTO v_method_statement_detail_id_" + statementDetails + ";\n");

            }


        }

//        //expression_config
        else if (object instanceof ExpressionConfigDTO) {
            expressions++;
            ExpressionConfigDTO expression = (ExpressionConfigDTO) object;
            String tableName = tableNames.get("expressionConfig");


            if (expression.getOperator() != null) {
                if (expression.getLeftOperand() != null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append(generateScript(expression.getRightOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperands + ",v_right_operand_id_" + rightOperands + ",v_operator_id_" + operators + ") RETURNING id INTO v_expression_id_" + expressions + ";\n");
                }
                else if (expression.getLeftOperand() == null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getRightOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() , NULL" + ",v_right_operand_id_" + rightOperands + ",v_operator_id_" + operators + ") RETURNING id INTO v_expression_id_" + expressions + ";\n");

                }
                else if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                    query.append(generateScript(expression.getOperator(), tableName));
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperands + ",NULL" + ",v_operator_id_" + operators + ") RETURNING id INTO v_expression_id_" + expressions + ";\n");


                }
            } else if (expression.getOperator() == null) {
                if (expression.getLeftOperand() == null && expression.getRightOperand() != null) {
                    query.append(generateScript(expression.getRightOperand(), tableName));

                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4(), NULL  " + ",v_right_operand_id_" + rightOperands + ",NULL ) RETURNING id INTO v_expression_id_" + expressions + ";\n");

                } else if (expression.getLeftOperand() != null && expression.getRightOperand() == null) {
                    query.append(generateScript(expression.getLeftOperand(), tableName));
                    query.append("INSERT INTO " + tableName + "(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_" + leftOperands + ",NULL" + " , NULL) RETURNING id INTO v_expression_id_" + expressions + ";\n");

                }
            }
        }

        // left operand_config
        else if (object instanceof LeftOperandConfigDTO) {
            leftOperands++;
            LeftOperandConfigDTO leftOperand = (LeftOperandConfigDTO) object;
            String tableName = tableNames.get("operand");

            if (leftOperand.getMethodToBeCalled() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,'" + leftOperand.getMethodToBeCalled() + "'," + "NULL" + ",'" + leftOperand.getType() + "'," + "NULL) RETURNING id INTO v_left_operand_id_" + leftOperands + ";\n");

                for (MethodArgumentsConfigDTO methodArguments : leftOperand.getMethodArgumentsConfigList()) {
                    query.append(generateScript(methodArguments, "left"));
                }
            } else if (leftOperand.getExpressionConfig() != null) {
                query.append(generateScript(leftOperand.getExpressionConfig(), "left"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressions + ",'" + leftOperand.getType() + "'," + "NULL) RETURNING id INTO v_left_operand_id_" + leftOperands + ";\n");
            } else if (leftOperand.getPathToObject() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + leftOperand.getPathToObject() + "',NULL,NULL,NULL,' " + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperands + ";\n");

            } else if (leftOperand.getLiteral() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + leftOperand.getLiteral() + "',NULL,NULL, '" + leftOperand.getType() + "',NULL) RETURNING id INTO v_left_operand_id_" + leftOperands + ";\n");
            }


            if(leftOperand.getQuery_config()!=null){
                query.append(generateScript(leftOperand.getQuery_config(),"right"));
                query.append("UPDATE "+tableName+" set query_config_id = v_query_id_"+methodquery+" WHERE query_config_id = NULL; \n");

            }
        }


        //right operand
        else if (object instanceof RightOperandConfigDTO) {
            rightOperands++;
            RightOperandConfigDTO rightOperand = (RightOperandConfigDTO) object;
            String tableName = tableNames.get("operand");

            if (rightOperand.getMethodToBeCalled() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,'" + rightOperand.getMethodToBeCalled() + "'," + "NULL" + ",'" + rightOperand.getType() + "'," + "NULL) RETURNING id INTO v_right_operand_id_" + rightOperands + ";");
                for (MethodArgumentsConfigDTO methodArguments : rightOperand.getMethodArgumentsConfigList()) {
                    query.append(generateScript(methodArguments, "right"));
                }
            } else if (rightOperand.getExpressionConfig() != null) {
                query.append(generateScript(rightOperand.getExpressionConfig(), "right"));
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,NULL," + "v_expression_id_" + expressions + ",'" + rightOperand.getType() + "'," + "NULL) RETURNING id INTO v_right_operand_id_" + rightOperands + ";");
            } else if (rightOperand.getPathToObject() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'" + rightOperand.getPathToObject() + "',NULL,NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperands + ";");

            } else if (rightOperand.getLiteral() != null) {
                query.append("INSERT INTO " + tableName + "(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'" + rightOperand.getLiteral() + "',NULL,NULL, '" + rightOperand.getType() + "',NULL) RETURNING id INTO v_right_operand_id_" + rightOperands + ";");
            }


            if(rightOperand.getQuery_config()!=null){
                query.append(generateScript(rightOperand.getQuery_config(),"right"));
                query.append("UPDATE "+tableName+" set query_config_id = v_query_id_"+methodquery+" WHERE query_config_id = NULL; \n");

            }

        }

        //operator_config
        else if (object instanceof OperatorDTO) {
            operators++;
            OperatorDTO operator = (OperatorDTO) object;
            String tableName = tableNames.get("operator");
            query.append("INSERT INTO " + tableName + "(id,is_archive,name,type) VALUES(" + "uuid_generate_v4(),'" + operator.is_archive() + "','" + operator.getName() + "','" + operator.getType() + "') ON CONFLICT (id)\n" +
                    "DO NOTHING  RETURNING ID INTO v_operator_id_" + operators + ";");
            if (parent.equals("config.m_expression_config")) {
                query.append(" UPDATE config.m_expression_config SET operator_id = v_operator_id_" + operators + " WHERE id = v_expression_id_" + expressions + ";\n");
            }
        }
//
        //method_Arguments_config
        else if (object instanceof MethodArgumentsConfigDTO) {
            methodArgument++;
            MethodArgumentsConfigDTO methodArguments = (MethodArgumentsConfigDTO) object;
            String tableName = tableNames.get("methodArgumentsConfigList");

            if (methodArguments.getExpressionConfig() != null) {
                query.append(generateScript(methodArguments.getExpressionConfig(), tableName));
                if (parent.equalsIgnoreCase("left")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_" + leftOperands + ",NULL"  + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressions + ")RETURNING ID INTO v_method_arguments_id_" + methodArgument + ";\n");
                } else if (parent.equalsIgnoreCase("right")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_" + rightOperands + ",NULL"+",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "v_expression_id_" + expressions + ")RETURNING ID INTO v_method_arguments_id_" + methodArgument + ";\n");
                }
            } else {
                if (parent.equalsIgnoreCase("left")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_"+leftOperands+",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgument + ";\n");
                } else if (parent.equalsIgnoreCase("right")) {
                    query.append("INSERT INTO " + tableName + "(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_right_operand_id_"+rightOperands+",NULL" + ",'" + methodArguments.getLiteral() + "','" + methodArguments.getPath_to_object() + "'," + "NULL)RETURNING id INTO v_method_arguments_id_" + methodArgument + ";\n");
                }
            }


        }
//
//        //method_variable
        else if (object instanceof MethodVariableDTO) {
            variable++;
            MethodVariableDTO methodVariable = (MethodVariableDTO) object;
            String tableName = tableNames.get("methodVariablesList");
            query.append("INSERT INTO " + tableName + "(id,variable_name,type,schema_def_Id,method_id) VALUES(" + "uuid_generate_v4(),'" + methodVariable.getVariable_name() + "','" + methodVariable.getType() + "','" + methodVariable.getSchema_def_id() + "'," + "v_method_id_" + methodConfig + ") RETURNING ID INTO v_method_variable_id_" + variable + ";\n");

        }
        else if(object instanceof QueryConfigDTO)
        {
            methodquery++;
            QueryConfigDTO queryConfig = (QueryConfigDTO)  object;
            String tableName = tableNames.get("methodquery");
            query.append("INSERT INTO " + tableName + "(id, query,databaseConfigId) VALUES( uuid_generate_v4(),'" + queryConfig.getQuery()+ "','"+queryConfig.getDatabaseConfigId()+"')RETURNING id INTO v_query_id_;\n");


        }

        return query.toString();
    }
}
