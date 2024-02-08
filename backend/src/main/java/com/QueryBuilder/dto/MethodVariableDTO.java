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
    private String variable_name;
    private String type;
    @JsonProperty(value = "schema_def_id")
    private String schema_def_id;
    @JsonProperty(value = "method_id")
    private UUID method_id;
}
