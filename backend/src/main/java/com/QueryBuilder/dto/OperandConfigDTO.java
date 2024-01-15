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
public class OperandConfigDTO {
    private UUID id;
    @JsonProperty(value = "path_to_object")
    private String pathToObject;
    private String literal;
    @JsonProperty(value = "method_to_be_called")
    private UUID methodToBeCalled;
    @JsonProperty(value = "expression_id")
    private UUID expressionId;
    private String type;
    private String query;
}
