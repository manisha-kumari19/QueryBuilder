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
public class BlockConfigDTO {
    private UUID id;
    @JsonProperty(value = "parent_block_id")
    private UUID parentBlockId;
    private List<MethodStatementDTO> methodStatementList;
}
