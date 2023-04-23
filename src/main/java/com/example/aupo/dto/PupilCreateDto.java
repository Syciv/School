package com.example.aupo.dto;

import lombok.Data;

@Data
public class PupilCreateDto {
    private String name;
    private String surname;
    private String patronymic;
    private Long groupId;
}
