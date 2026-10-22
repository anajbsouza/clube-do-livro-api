package br.com.anajulia.clubedolivro.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record BookRequest(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 200, message = "O título pode ter no máximo 200 caracteres")
        String title,

        @NotBlank(message = "A autora é obrigatória")
        @Size(max = 150, message = "A autora pode ter no máximo 150 caracteres")
        String author,

        @Min(value = 1000, message = "Ano de publicação inválido")
        @Max(value = 2100, message = "Ano de publicação inválido")
        Integer publicationYear,

        @NotNull(message = "A data do encontro é obrigatória")
        LocalDate meetingDate,

        Boolean authorPresent
) {
}