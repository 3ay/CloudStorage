package ru.netology.cloudstorage.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data
@Validated
public class LoginCredentials {
    @NotBlank
    private String login;
    @NotBlank
    private String password;
}
