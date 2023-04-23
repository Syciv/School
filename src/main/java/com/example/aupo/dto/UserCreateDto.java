package com.example.aupo.dto;

import lombok.Data;

@Data
public class UserCreateDto {

    private String login;
    private String password;
    private String name;
    private String surname;
    private String patronymic;
    private Boolean isAdmin;

}
