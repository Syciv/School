package com.example.aupo.controller.pupil;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.controller.group.GroupCreateDto;
import com.example.aupo.controller.group.GroupRepository;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.exception.ValidationException;
import com.example.aupo.tables.daos.GroupDao;
import com.example.aupo.tables.daos.PupilDao;
import com.example.aupo.tables.pojos.Group;
import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.tables.records.PupilRecord;
import com.example.aupo.util.CSVUtil;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Group.GROUP;
import static com.example.aupo.tables.Parallel.PARALLEL;
import static com.example.aupo.tables.Pupil.PUPIL;
import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class PupilRestService {

    private final PupilRepository pupilRepository;

    private final GroupRepository groupRepository;

    private final PupilDao pupilDao;

    private final GroupDao groupDao;

    public void createPupil(PupilCreateDto pupilCreateDto){
        validate(pupilCreateDto);
        Pupil pupil = getPupilPojoFromCreateDto(pupilCreateDto);
        Long entityId = pupilRepository.getNextId();
        pupil.setId(entityId);
        pupil.setEntityId(entityId);
        pupilDao.insert(pupil);
    }

    public ResponseList<Pupil> getList(
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

        List<Pupil> items = pupilRepository.fetch(condition, page, pageSize);
        Long total = pupilRepository.getCount(condition);

        ResponseList<Pupil> result = new ResponseList<>();
        result.setItems(items);
        result.setTotal(total);
        return result;
    }

    @Transactional
    public void saveFromCSV(String fileContent) {
        List<String[]> values = CSVUtil.parseCSV(fileContent, ";");
        List<PupilRecord> pupilRecordList = values.stream().map(this::getPupilRecordFromCSVLine).toList();
        validateList(pupilRecordList);
        pupilRepository.updateDateTimeOfDeleteByIds(
                pupilRecordList.stream().map(PupilRecord::getEntityId).toList(),
                LocalDateTime.now());
        pupilRepository.batchInsert(pupilRecordList);
    }

    private PupilRecord getPupilRecordFromCSVLine(String[] elements){
        PupilRecord pupilRecord = new PupilRecord();
        pupilRecord.setEntityId(Long.parseLong(elements[0]));
        pupilRecord.setSurname(elements[1]);
        pupilRecord.setName(elements[2]);
        pupilRecord.setPatronymic(elements[3]);
        pupilRecord.setGroupEntityId(Long.parseLong(elements[4]));
        pupilRecord.setDatetimeOfCreation(LocalDateTime.now());
        return pupilRecord;
    }

    private Pupil getPupilPojoFromCreateDto(PupilCreateDto pupilCreateDto){
        Pupil pupil = new Pupil();
        pupil.setName(pupilCreateDto.getName());
        pupil.setSurname(pupilCreateDto.getSurname());
        pupil.setPatronymic(pupilCreateDto.getPatronymic());
        pupil.setGroupEntityId(pupilCreateDto.getGroupId());
        pupil.setDatetimeOfCreation(LocalDateTime.now());
        return pupil;
    }

    private PupilRecord getPupilRecordFromPojo(Pupil pupil){
        PupilRecord pupilRecord = new PupilRecord();
        pupilRecord.setEntityId(pupil.getEntityId());
        pupilRecord.setName(pupil.getName());
        pupilRecord.setSurname(pupil.getSurname());
        pupilRecord.setPatronymic(pupil.getPatronymic());
        pupilRecord.setGroupEntityId(pupil.getGroupEntityId());
        pupilRecord.setDatetimeOfCreation(LocalDateTime.now());
        return pupilRecord;
    }

    public Pupil getOne(Long entityId){
        return pupilRepository.fetchActualByEntityId(entityId).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void updatePupil(Pupil pupil) {
        Condition condition = PUPIL.ENTITY_ID.eq(pupil.getEntityId()).and(PUPIL.DATETIME_OF_DELETE.isNull());
        pupilRepository.updateDateTimeOfDeleteByCondition(condition, LocalDateTime.now());
        pupilDao.insert(pupil);
    }

    @Transactional
    public void migrate(List<Long> pupilIds, Long groupId) {
        validateGroup(groupId);
        List<PupilRecord> newPupils = new ArrayList<>();
        List<Pupil> oldPupils = pupilRepository.fetch(PUPIL.DATETIME_OF_DELETE.isNull().and(PUPIL.ENTITY_ID.in(pupilIds)));
        oldPupils.forEach(p -> {
           p.setId(null);
           p.setGroupEntityId(groupId);
           newPupils.add(getPupilRecordFromPojo(p));
        });

        pupilRepository.updateDateTimeOfDeleteByIds(pupilIds, LocalDateTime.now());
        pupilRepository.batchInsert(newPupils);
    }

    private void validate(PupilCreateDto pupilCreateDto){
        StringBuilder stringBuilder = new StringBuilder();
        if(!groupRepository.exists(GROUP.ENTITY_ID.eq(pupilCreateDto.getGroupId())
                .and(GROUP.DATETIME_OF_DELETE.isNull()))){
            stringBuilder.append("Группы не существует");
            stringBuilder.append("\n");
        }
        if(!stringBuilder.isEmpty()){
            throw new ValidationException(stringBuilder.toString());
        }
    }

    private void validateGroup(Long groupEntityId) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!groupRepository.exists(GROUP.ENTITY_ID.eq(groupEntityId).and(GROUP.DATETIME_OF_DELETE.isNull()))) {
            stringBuilder.append("Группы не существует");
            stringBuilder.append("\n");
        }
        if (!stringBuilder.isEmpty()) {
            throw new ValidationException(stringBuilder.toString());
        }
    }

    private void validateList(List<PupilRecord> pupilRecordList){
        StringBuilder stringBuilder = new StringBuilder();
        List<Long> pupilGroups = pupilRecordList.stream().map(PupilRecord::getGroupEntityId).toList();
        List<Long> existingGroups = groupRepository.fetch(GROUP.DATETIME_OF_DELETE.isNull()
                        .and(GROUP.ENTITY_ID.in(pupilGroups)))
                .stream().map(Group::getEntityId).toList();
        if(!new HashSet<>(existingGroups).equals(new HashSet<>(pupilGroups))){
            stringBuilder.append("Несуществующая группа");
            stringBuilder.append("\n");
        }

        if(!stringBuilder.isEmpty()){
            throw new ValidationException(stringBuilder.toString());
        }
    }
}
