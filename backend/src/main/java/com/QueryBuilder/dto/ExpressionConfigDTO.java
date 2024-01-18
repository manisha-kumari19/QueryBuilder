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
    private UUID left_operand_id;
    @JsonProperty(value = "right_operand_id")
    private UUID right_operand_id;
    @JsonProperty(value = "operator_id")
    private UUID operator_id;
    private OperatorDTO operator;
    private OperandConfigDTO leftOperand;
    private OperandConfigDTO rightOperand;
}
