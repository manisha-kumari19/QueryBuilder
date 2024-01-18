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
public class MethodStatementDetailDTO {
    private UUID id;
    @JsonProperty(value = "method_statement_id")
    private UUID method_statement_id;
    private int sequence;
    @JsonProperty(value = "method_statement_expression_type")
    private String method_statement_expression_type;
    @JsonProperty(value = "expression_id")
    private UUID expression_id;
    @JsonProperty(value = "block_id")
    private UUID block_id;
    @JsonProperty(value = "blockConfig")
    private BlockConfigDTO blockConfig;
    @JsonProperty(value = "expressionConfig")
    private ExpressionConfigDTO expressionConfig;
}
