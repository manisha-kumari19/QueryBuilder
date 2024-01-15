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
public class MethodVariableDTO {

    private UUID id;
    @JsonProperty(value = "variable_name")
    private String variableName;
    private String type;
    @JsonProperty(value = "schema_def_id")
    private UUID schemaDefId;
    @JsonProperty(value = "method_id")
    private UUID methodId;
}
