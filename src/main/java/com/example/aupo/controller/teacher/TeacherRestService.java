package com.example.aupo.controller.teacher;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.tables.daos.TeacherDao;
import com.example.aupo.tables.pojos.Teacher;
import com.example.aupo.tables.records.TeacherRecord;
import com.example.aupo.util.CSVUtil;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class TeacherRestService {

    private final TeacherRepository teacherRepository;
    
    private final TeacherDao teacherDao;

    public void createTeacher(TeacherCreateDto teacherCreateDto){
        Teacher teacher = getTeacherPojoFromCreateDto(teacherCreateDto);
        Long entityId = teacherRepository.getNextId();
        teacher.setId(entityId);
        teacher.setEntityId(entityId);
        teacherDao.insert(teacher);
    }
    
    public Teacher getOne(Long entityId) throws NotFoundException {
        return teacherRepository.fetchActualByEntityId(entityId).orElseThrow(NotFoundException::new);
    }
    
    public ResponseList<Teacher> getList(
            Integer page,
            Integer pageSize,
            String name,
            String surname,
            String patronymic
    ) {
        Condition condition = TEACHER.DATETIME_OF_DELETE.isNull();
        if(Objects.nonNull(name)){
            condition = condition.and(TEACHER.NAME.containsIgnoreCase(name));
        }
        if(Objects.nonNull(surname)){
            condition = condition.and(TEACHER.SURNAME.containsIgnoreCase(surname));
        }
        if(Objects.nonNull(patronymic)){
            condition = condition.and(TEACHER.PATRONYMIC.containsIgnoreCase(patronymic));
        }
        List<Teacher> items = teacherRepository.fetch(condition, page, pageSize);
        Long total = teacherRepository.getCount(condition);
        ResponseList<Teacher> result = new ResponseList<>();
        result.setItems(items);
        result.setTotal(total);
        return result;
    }


    @Transactional
    public void saveFromCSV(String fileContent) {
        List<String[]> values = CSVUtil.parseCSV(fileContent, ";");
        List<TeacherRecord> teacherRecordList = values.stream().map(this::getTeacherRecordFromCSVLine).toList();
        teacherRepository.updateDateTimeOfDeleteByIds(
                teacherRecordList.stream().map(TeacherRecord::getEntityId).toList(),
                LocalDateTime.now());
        teacherRepository.batchInsert(teacherRecordList);
    }

    private TeacherRecord getTeacherRecordFromCSVLine(String[] elements){
        TeacherRecord teacherRecord = new TeacherRecord();
        teacherRecord.setEntityId(Long.parseLong(elements[0]));
        teacherRecord.setSurname(elements[1]);
        teacherRecord.setName(elements[2]);
        teacherRecord.setPatronymic(elements[3]);
        teacherRecord.setDatetimeOfCreation(LocalDateTime.now());
        return teacherRecord;
    }
    
    private Teacher getTeacherPojoFromCreateDto(TeacherCreateDto teacherCreateDto){
        Teacher teacher = new Teacher();
        teacher.setName(teacherCreateDto.getName());
        teacher.setSurname(teacherCreateDto.getSurname());
        teacher.setPatronymic(teacherCreateDto.getPatronymic());
        teacher.setDatetimeOfCreation(LocalDateTime.now());
        return teacher;
    }

    @Transactional
    public void updateTeacher(Teacher teacher) {
        Condition condition = TEACHER.ENTITY_ID.eq(teacher.getEntityId()).and(TEACHER.DATETIME_OF_DELETE.isNull());
        teacher.setId(null);
        teacherRepository.updateDateTimeOfDeleteByCondition(condition, LocalDateTime.now());
        teacherDao.insert(teacher);
    }
}
