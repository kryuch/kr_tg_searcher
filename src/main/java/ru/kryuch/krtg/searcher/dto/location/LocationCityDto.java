package ru.kryuch.krtg.searcher.dto.location;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LocationCityDto {

    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotNull
    private Long stateId;

    /** Заполняется при чтении, при сохранении игнорируется. */
    private String stateName;
}