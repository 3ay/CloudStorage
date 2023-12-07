package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    @JsonProperty("auth-token")
    private String authToken;
}
