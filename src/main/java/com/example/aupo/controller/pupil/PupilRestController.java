package com.example.aupo.controller.pupil;

import com.example.aupo.tables.pojos.Pupil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/pupil")
@AllArgsConstructor
public class PupilRestController {

    private final PupilRestService pupilRestService;

    @GetMapping(value = "/list")
    public List<Pupil> list(){
        return pupilRestService.getList();
    }

    @PostMapping
    public void create(@RequestBody PupilCreateDto pupilCreateDto){
        pupilRestService.createPupil(pupilCreateDto);
    }

}
