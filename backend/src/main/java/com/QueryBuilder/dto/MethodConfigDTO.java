package com.QueryBuilder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.*;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MethodConfigDTO {
    private UUID id;
    private String name;
    @JsonProperty(value = "is_archive")
    private boolean is_archive;
    @JsonProperty(value = "return_type_schema_def_id")
    private UUID return_type_schema_def_id;
    @JsonProperty(value = "block_id")
    private UUID block_id;
    private String type;
    @JsonProperty(value = "package_name")
    private String package_name;
    private List<MethodVariableDTO> methodVariablesList;
    private BlockConfigDTO blockConfig;
}
