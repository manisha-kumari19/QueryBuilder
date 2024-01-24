package com.QueryBuilder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.UUID;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LeftOperandConfigDTO {
    private UUID id;
    @JsonProperty(value = "path_to_object")
    private String pathToObject;
    private String literal;
    @JsonProperty(value = "method_to_be_called")
    private UUID methodToBeCalled;
    @JsonProperty(value = "expression_id")
    private UUID expressionId;
    private String type;
    private UUID query_config_id;
    private QueryConfigDTO query_config;
    private List<MethodArgumentsConfigDTO> methodArgumentsConfigList;
    private ExpressionConfigDTO expressionConfig;
}