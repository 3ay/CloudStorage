package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Validated
public class LoginResponseDTO {
    @NotBlank
    @JsonProperty("auth-token")
    private String authToken;
}
