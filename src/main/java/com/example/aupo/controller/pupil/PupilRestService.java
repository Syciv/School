package com.example.aupo.controller.pupil;

import com.example.aupo.tables.daos.PupilDao;
import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.tables.records.PupilRecord;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Pupil.PUPIL;
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
        pupil.setGroupEntityId(pupilCreateDto.getGroupId());
        pupil.setDatetimeOfCreation(LocalDateTime.now());
        pupilDao.insert(pupil);
    }

    public List<Pupil> getList(
            Integer page,
            Integer pageSize,
            String name,
            String surname,
            String patronymic,
            Long groupId
    ) {
        Condition condition = PUPIL.DATETIME_OF_DELETE.isNull();
        if(Objects.nonNull(name)){
            condition = condition.and(PUPIL.NAME.containsIgnoreCase(name));
        }
        if(Objects.nonNull(surname)){
            condition = condition.and(PUPIL.SURNAME.containsIgnoreCase(surname));
        }
        if(Objects.nonNull(patronymic)){
            condition = condition.and(PUPIL.PATRONYMIC.containsIgnoreCase(patronymic));
        }
        if(Objects.nonNull(groupId)){
            condition = condition.and(PUPIL.GROUP_ENTITY_ID.eq(groupId));
        }
        return pupilRepository.fetch(condition, page, pageSize);
    }

    @Transactional
    public void saveFromCSV(String fileContent) {
        String[] lines = fileContent.split("\r\n");
        List<PupilRecord> pupilRecordList = new ArrayList<>();
        for(String line : lines){
            pupilRecordList.add(getRecordFromCSVLine(line));
        }
        pupilRepository.updateDateTimeOfDeleteByIds(
                pupilRecordList.stream().map(PupilRecord::getEntityId).toList(),
                LocalDateTime.now());

        pupilRepository.batchInsert(pupilRecordList);
    }

    private PupilRecord getRecordFromCSVLine(String line){
        PupilRecord pupilRecord = new PupilRecord();
        String[] elements = line.split(";");
        pupilRecord.setEntityId(Long.parseLong(elements[0]));
        pupilRecord.setSurname(elements[1]);
        pupilRecord.setName(elements[2]);
        pupilRecord.setPatronymic(elements[3]);
        pupilRecord.setGroupEntityId(Long.parseLong(elements[4]));
        pupilRecord.setDatetimeOfCreation(LocalDateTime.now());
        return pupilRecord;
    }

}
