package com.QueryBuilder.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PackageNameDTO {
    private String type;
    private String value;
    private boolean isNull;
}

