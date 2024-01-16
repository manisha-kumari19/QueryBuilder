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
    private UUID methodStatementId;
    private int sequence;
    @JsonProperty(value = "method_statement_expression_type")
    private String methodStatementExpressionType;
    @JsonProperty(value = "expression_id")
    private UUID expressionId;
    @JsonProperty(value = "block_id")
    private UUID blockId;
    @JsonProperty(value = "blockConfig")
    private BlockConfigDTO blockConfig;
    @JsonProperty(value = "expressionConfig")
    private ExpressionConfigDTO expressionConfig;
}
