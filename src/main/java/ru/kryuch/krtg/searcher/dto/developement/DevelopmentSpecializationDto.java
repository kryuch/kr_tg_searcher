package ru.kryuch.krtg.searcher.dto.developement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DevelopmentSpecializationDto {

    private Long id;

    @NotBlank
    @Size(max = 10)
    private String code;

    @NotBlank
    @Size(max = 255)
    private String name;
}