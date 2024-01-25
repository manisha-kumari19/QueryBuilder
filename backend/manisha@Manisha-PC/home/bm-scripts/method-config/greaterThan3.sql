DO $$ 
 DECLARE 

 BEGIN 
INSERT INTO config.m_method_config(id,name,is_archive,return_type_schema_def_id,block_id,package_name,type) VALUES (uuid_generate_v4(),'greaterThan3','false','45a18d55-4555-4316-866f-82d81269bc40',NULL,'bm','CONFIGURED_METHOD') RETURNING id INTO v_method_id_1;
INSERT INTO config.m_method_variables(id,variable_name,type,schema_def_Id,method_id) VALUES(uuid_generate_v4(),'a','PARAM','7433d798-93fb-4121-9034-7071361dc3cf',v_method_id_1) RETURNING ID INTO v_method_variable_id_1;
INSERT INTO config.m_block_config (id,parent_block_id) VALUES (uuid_generate_v4(),null ) RETURNING id INTO v_block_id_1;
 UPDATE config.m_method_config SET block_id = v_block_id_1 WHERE id = v_method_id_1;
INSERT INTO config.m_method_statement (id,type,sequence,block_id) VALUES (uuid_generate_v4(),'IF','0',v_block_id_1)RETURNING id INTO v_method_statement_id_1;
INSERT INTO config.m_block_config (id,parent_block_id) VALUES (uuid_generate_v4(),null ) RETURNING id INTO v_block_id_2;
INSERT INTO config.m_method_statement (id,type,sequence,block_id) VALUES (uuid_generate_v4(),'BASIC_STATEMENT','0',v_block_id_2)RETURNING id INTO v_method_statement_id_2;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'true',NULL,NULL, 'LITERAL',NULL) RETURNING id INTO v_left_operand_id_1;
INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_1,NULL , NULL) RETURNING id INTO v_expression_id_1;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_2,'0','RETURN', v_expression_id_1,NULL) RETURNING id INTO v_method_statement_detail_id_2;
 UPDATE config.m_method_statement_detail SET block_id = v_block_id_2 WHERE id = v_method_statement_detail_id_2;
INSERT INTO config.m_operators(id,is_archive,name,type) VALUES(uuid_generate_v4(),'false','GREATERTHAN','relational') ON CONFLICT (id)
DO NOTHING  RETURNING ID INTO v_operator_id_1; UPDATE config.m_expression_config SET operator_id = v_operator_id_1 WHERE id = v_expression_id_2;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'a',NULL,NULL,NULL,' PATH',NULL) RETURNING id INTO v_left_operand_id_2;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'3',NULL,NULL, 'LITERAL',NULL) RETURNING id INTO v_right_operand_id_1;INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_2,v_right_operand_id_1,v_operator_id_1) RETURNING id INTO v_expression_id_2;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_2,'0','IF', v_expression_id_2,v_block_id_2) RETURNING id INTO v_method_statement_detail_id_2;
INSERT INTO config.m_block_config (id,parent_block_id) VALUES (uuid_generate_v4(),null ) RETURNING id INTO v_block_id_3;
INSERT INTO config.m_method_statement (id,type,sequence,block_id) VALUES (uuid_generate_v4(),'BASIC_STATEMENT','0',v_block_id_3)RETURNING id INTO v_method_statement_id_3;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'false',NULL,NULL, 'LITERAL',NULL) RETURNING id INTO v_left_operand_id_3;
INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_3,NULL , NULL) RETURNING id INTO v_expression_id_3;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_3,'0','RETURN', v_expression_id_3,NULL) RETURNING id INTO v_method_statement_detail_id_4;
 UPDATE config.m_method_statement_detail SET block_id = v_block_id_3 WHERE id = v_method_statement_detail_id_4;
INSERT INTO config.m_operators(id,is_archive,name,type) VALUES(uuid_generate_v4(),'false','LESSTHAN','relational') ON CONFLICT (id)
DO NOTHING  RETURNING ID INTO v_operator_id_2; UPDATE config.m_expression_config SET operator_id = v_operator_id_2 WHERE id = v_expression_id_4;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'a',NULL,NULL,NULL,' PATH',NULL) RETURNING id INTO v_left_operand_id_4;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'3',NULL,NULL, 'LITERAL',NULL) RETURNING id INTO v_right_operand_id_2;INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_4,v_right_operand_id_2,v_operator_id_2) RETURNING id INTO v_expression_id_4;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_3,'1','ELSE_IF', v_expression_id_4,v_block_id_3) RETURNING id INTO v_method_statement_detail_id_4;
INSERT INTO config.m_block_config (id,parent_block_id) VALUES (uuid_generate_v4(),null ) RETURNING id INTO v_block_id_4;
INSERT INTO config.m_method_statement (id,type,sequence,block_id) VALUES (uuid_generate_v4(),'BASIC_STATEMENT','0',v_block_id_4)RETURNING id INTO v_method_statement_id_4;
INSERT INTO config.m_operators(id,is_archive,name,type) VALUES(uuid_generate_v4(),'false','ASSIGNMENT','arithmetic') ON CONFLICT (id)
DO NOTHING  RETURNING ID INTO v_operator_id_3; UPDATE config.m_expression_config SET operator_id = v_operator_id_3 WHERE id = v_expression_id_5;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,'a',NULL,NULL,NULL,' PATH',NULL) RETURNING id INTO v_left_operand_id_5;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() ,NULL,'4',NULL,NULL, 'LITERAL',NULL) RETURNING id INTO v_right_operand_id_3;INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_5,v_right_operand_id_3,v_operator_id_3) RETURNING id INTO v_expression_id_5;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_4,'0','EXPRESSION', v_expression_id_5,NULL) RETURNING id INTO v_method_statement_detail_id_6;
INSERT INTO config.m_method_statement (id,type,sequence,block_id) VALUES (uuid_generate_v4(),'BASIC_STATEMENT','1',v_block_id_4)RETURNING id INTO v_method_statement_id_5;
INSERT INTO config.m_operand_config(id,path_to_object,literal,method_to_be_called,expression_id,type,query_config_id) VALUES ( uuid_generate_v4() , NULL ,NULL,'b8c27a20-ca92-4afe-a314-d30ab4fca029',NULL,'METHOD_CALL',NULL) RETURNING id INTO v_left_operand_id_6;
INSERT INTO config.m_method_arguments_config(id,operand_id,method_variable_id,literal,path_to_object,expression_id)VALUES(uuid_generate_v4(),v_left_operand_id_6,NULL,'null','a',NULL)RETURNING id INTO v_method_arguments_id_1;
INSERT INTO config.m_expression_config(id,left_operand_id,right_operand_id,operator_id) VALUES (uuid_generate_v4() ,v_left_operand_id_6,NULL , NULL) RETURNING id INTO v_expression_id_6;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_5,'0','RETURN', v_expression_id_6,NULL) RETURNING id INTO v_method_statement_detail_id_7;
 UPDATE config.m_method_statement_detail SET block_id = v_block_id_4 WHERE id = v_method_statement_detail_id_7;
INSERT INTO config.m_method_statement_detail (id,method_statement_id,sequence,method_statement_expression_type,expression_id,block_id) VALUES ( uuid_generate_v4(),v_method_statement_id_5,'2','ELSE',NULL,v_block_id_4) RETURNING id INTO v_method_statement_detail_id_7;

COMMIT; 
END $$;