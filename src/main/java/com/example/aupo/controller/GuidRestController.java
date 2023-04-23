package com.example.aupo.controller;

import com.example.aupo.service.GuideService;
import com.example.aupo.tables.pojos.Level;
import com.example.aupo.tables.pojos.Parallel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для просмотра справочников
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/guide")
public class GuidRestController {

    private final GuideService guideService;

    @GetMapping(value = "/parallel")
    public List<Parallel> getParallels(){
        return guideService.parallelList();
    }

    @GetMapping(value = "/level")
    public List<Level> getLevels(){
        return guideService.getLevelList();
    }

}
