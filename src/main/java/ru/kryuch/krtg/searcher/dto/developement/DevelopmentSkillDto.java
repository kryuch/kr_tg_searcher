package ru.kryuch.krtg.searcher.dto.developement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevelopmentSkillDto {

    private Long id;

    @NotBlank
    @Size(max = 8)
    private String code;

    @NotBlank
    @Size(max = 16)
    private String name;
}
