package com.QueryBuilder.dto;

import lombok.*;

import java.util.UUID;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MethodArgumentsConfigDTO {

    private UUID id;
    private UUID operandId;
    private UUID methodVariableId;
    private String literal;
    private String pathToObject;
    private UUID expressionId;
}
