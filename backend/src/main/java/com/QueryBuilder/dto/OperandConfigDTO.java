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
public class OperandConfigDTO {
    private UUID id;
    @JsonProperty(value = "path_to_object")
    private String path_to_object;
    private String literal;
    @JsonProperty(value = "method_to_be_called")
    private UUID method_to_be_called;
    @JsonProperty(value = "expression_id")
    private UUID expression_id;
    private String type;
    private UUID query_config_id;
    private QueryConfigDTO queryConfig;
    private List<MethodArgumentsConfigDTO> methodArgumentsConfigList;
    private ExpressionConfigDTO expressionConfig;
}
