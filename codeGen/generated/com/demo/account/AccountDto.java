package com.demo.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    @JsonProperty("accountId")
    private Integer accountId;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("accountName")
    private String accountName;
    @JsonProperty("accountType")
    private String accountType;
    @JsonProperty("balance")
    private Double balance;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("createdAt")
    private String createdAt;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("updatedAt")
    private String updatedAt;
    @JsonProperty("updatedBy")
    private String updatedBy;
}
