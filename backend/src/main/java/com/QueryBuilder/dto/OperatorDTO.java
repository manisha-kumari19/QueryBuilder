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
public class OperatorDTO {
    public UUID id;
    @JsonProperty(value = "is_archive")
    public boolean is_archive;
    public String name;
    public String type;
}
