package com.example.demo.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    @JsonProperty("id")
    private Object id;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("name")
    private String name;
    @JsonProperty("money")
    private Integer money;
}
