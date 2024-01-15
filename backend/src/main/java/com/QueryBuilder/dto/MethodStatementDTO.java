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
public class MethodStatementDTO {
    private UUID id;
    private String type;
    private int sequence;
    @JsonProperty(value = "block_id")
    private UUID blockId;
    private List<MethodStatementDetailDTO> methodStatementDetailList;
}