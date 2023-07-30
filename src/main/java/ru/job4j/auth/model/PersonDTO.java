package ru.job4j.auth.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PersonDTO {
    @Size(message = "Password should be at least 6 characters", min = 6, max = 50)
    @NotNull
    private String password;
}
