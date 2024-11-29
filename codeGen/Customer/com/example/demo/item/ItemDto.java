package com.example.demo.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDto {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
}
