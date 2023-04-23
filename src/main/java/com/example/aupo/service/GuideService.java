package com.example.aupo.service;

import com.example.aupo.repository.GuideRepository;
import com.example.aupo.tables.pojos.Level;
import com.example.aupo.tables.pojos.Parallel;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.jooq.impl.DSL.trueCondition;

@Service
@AllArgsConstructor
public class GuideService {

    private final GuideRepository guideRepository;

    public List<Parallel> parallelList(){
        Condition condition = trueCondition();
        return guideRepository.fetchParallels(condition);
    }

    public List<Level> getLevelList() {
        Condition condition = trueCondition();
        return guideRepository.fetchLevels(condition);
    }
}




