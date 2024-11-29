package com.example.demo.transfer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferDto {
    @JsonProperty("fromAccount")
    private String fromAccount;
    @JsonProperty("toAccount")
    private String toAccount;
    @JsonProperty("money")
    private Integer money;
}
