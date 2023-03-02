package com.example.aupo.controller.pupil;

import lombok.Data;

import java.util.List;

@Data
public class PupilMigrateDto {

    private List<Long> pupilIds;
    private Long groupId;

}
