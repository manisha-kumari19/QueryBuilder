package com.QueryBuilder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MethodArgumentsConfigDTO {

    private UUID id;
    @JsonProperty(value="operand_id")
    private UUID operandId;
    @JsonProperty(value="method_variable_id")
    private UUID methodVariableId;
    private String literal;
    @JsonProperty(value="path_to_object")
    private String pathToObject;
    @JsonProperty(value="expression_id")
    private UUID expressionId;
    private MethodVariableDTO methodVariables;
    private ExpressionConfigDTO expressionConfig;
}
