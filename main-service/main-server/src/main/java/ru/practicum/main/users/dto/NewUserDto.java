package ru.practicum.main.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserDto {
    @Email
    @NotEmpty
    @Size(min = 6, max = 254)
    private String email;

    @NotEmpty
    @Size(min = 2, max = 250)
    private String name;
}