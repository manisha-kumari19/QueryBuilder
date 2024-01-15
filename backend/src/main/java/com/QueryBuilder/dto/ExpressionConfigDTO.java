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
public class ExpressionConfigDTO {
    private UUID id;
    @JsonProperty(value = "left_operand_id")
    private UUID leftOperandId;
    @JsonProperty(value = "right_operand_id")
    private UUID rightOperandId;
    @JsonProperty(value = "operator_id")
    private UUID operatorId;
}
