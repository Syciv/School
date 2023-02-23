package com.example.aupo.controller.pupil;

import com.example.aupo.tables.daos.PupilDao;
import com.example.aupo.tables.pojos.Pupil;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.jooq.impl.DSL.trueCondition;

@Service
@AllArgsConstructor
public class PupilRestService {

    private final PupilRepository pupilRepository;

    private final PupilDao pupilDao;

    public void createPupil(PupilCreateDto pupilCreateDto){
        Pupil pupil = new Pupil();
        pupil.setName(pupilCreateDto.getName());
        pupil.setSurname(pupilCreateDto.getSurname());
        pupil.setPatronymic(pupilCreateDto.getPatronymic());
        pupil.setGroupId(pupilCreateDto.getGroupId());
        pupil.setDatetimeOfCreation(LocalDateTime.now());
        pupilDao.insert(pupil);
    }

    public List<Pupil> getList(){
        return pupilRepository.getList(trueCondition());
    }

}
