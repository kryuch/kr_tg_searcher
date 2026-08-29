package ru.kryuch.krtg.searcher.dto.vacancy;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtensionLoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}