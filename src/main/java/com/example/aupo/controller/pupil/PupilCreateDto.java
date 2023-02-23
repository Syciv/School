package com.example.aupo.controller.pupil;

import lombok.Data;

@Data
public class PupilCreateDto {
    private String name;
    private String surname;
    private String patronymic;
    private Long groupId;
}
