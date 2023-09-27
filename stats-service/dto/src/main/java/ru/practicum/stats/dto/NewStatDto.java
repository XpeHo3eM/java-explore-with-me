package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.general.util.Constants.TIME_FORMAT;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewStatDto {
    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    @Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
    private String ip;

    @NotNull
    @JsonFormat(pattern = TIME_FORMAT)
    private LocalDateTime timestamp;
}
