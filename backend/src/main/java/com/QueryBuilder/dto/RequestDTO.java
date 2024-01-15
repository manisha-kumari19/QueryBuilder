package com.QueryBuilder.dto;

import lombok.*;

import java.util.List;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDTO {
    private List<MethodConfigDTO> methodConfigs;
}
