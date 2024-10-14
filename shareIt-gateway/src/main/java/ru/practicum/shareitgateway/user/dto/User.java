package ru.practicum.shareitgateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class User {
    private long id;
    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
}
