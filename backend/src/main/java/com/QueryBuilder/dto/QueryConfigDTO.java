package com.QueryBuilder.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryConfigDTO {
    private UUID id;
    private String query;
    private UUID databaseConfigId;
}
