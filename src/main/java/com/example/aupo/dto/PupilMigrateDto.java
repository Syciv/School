package com.example.aupo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PupilMigrateDto {

    private List<Long> pupilIds;
    private Long groupId;

}
