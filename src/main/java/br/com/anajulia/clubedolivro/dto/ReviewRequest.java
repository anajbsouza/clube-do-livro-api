package br.com.anajulia.clubedolivro.dto;

import br.com.anajulia.clubedolivro.entity.ReadingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull(message = "A situação da leitura é obrigatória")
        ReadingStatus readingStatus,

        // Opcional: ausente vale false
        Boolean attendedMeeting,

        @Min(value = 1, message = "A nota vai de 1 a 5")
        @Max(value = 5, message = "A nota vai de 1 a 5")
        Integer rating,

        @Size(max = 2000, message = "O comentário pode ter no máximo 2000 caracteres")
        String comment
) {
}