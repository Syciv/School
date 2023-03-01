package com.example.aupo.controller.guide;

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

    private final GuideRestService guideRestService;

    @GetMapping(value = "/parallel")
    public List<Parallel> getParallels(){
        return guideRestService.parallelList();
    }

    @GetMapping(value = "/level")
    public List<Level> getLevels(){
        return guideRestService.getLevelList();
    }

}
