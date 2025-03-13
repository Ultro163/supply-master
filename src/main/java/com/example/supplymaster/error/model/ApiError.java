package com.example.supplymaster.error.model;


import com.example.supplymaster.util.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель, представляющая структуру ошибки, которая отправляется в ответе на запросы,
 * когда происходит исключение в приложении. Она содержит информацию о сообщении ошибки,
 * причине, статусе и времени возникновения ошибки.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Модель для представления ошибки, которая отправляется в ответ на запросы в случае исключений. " +
        "Содержит сообщение ошибки, причину, статус и время возникновения.")
public class ApiError {

    @Schema(description = "Список ошибок, которые произошли в приложении",
            example = "[\"Некорректные данные в запросе\", \"Ошибка аутентификации\"]")
    @Builder.Default
    List<String> errors = new ArrayList<>();

    @Schema(description = "Сообщение, описывающее ошибку", example = "Невозможно найти запрашиваемый ресурс")
    String message;

    @Schema(description = "Причина ошибки", example = "Ресурс не существует")
    String reason;

    @Schema(description = "Статус ошибки", example = "404 NOT_FOUND")
    String status;

    @Schema(description = "Время возникновения ошибки",
            example = "2025-01-24T12:30:00", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    OffsetDateTime timestamp;
}