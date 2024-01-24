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
    @JsonProperty(value = "operand_id")
    private UUID operand_id;
    @JsonProperty(value = "method_variable_id")
    private UUID method_variable_id;
    private String literal;
    @JsonProperty(value = "path_to_object")
    private String path_to_object;
    private UUID expression_id;
    private MethodVariableDTO methodVariables;
    private ExpressionConfigDTO expressionConfig;
}
