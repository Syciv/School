package com.example.aupo.service;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.dto.TeacherCreateDto;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.repository.TeacherRepository;
import com.example.aupo.sort.TeacherSortEnum;
import com.example.aupo.tables.daos.TeacherDao;
import com.example.aupo.tables.pojos.Teacher;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class TeacherService {

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
            String patronymic,
            TeacherSortEnum teacherSortEnum,
            SortOrder sortOrder,
            String search
    ) {

        SortField<?> sortField = switch (teacherSortEnum){
            case ID -> TEACHER.ID.sort(sortOrder);
            case NAME -> TEACHER.NAME.sort(sortOrder);
            case SURNAME -> TEACHER.SURNAME.sort(sortOrder);
            case PATRONYMIC -> TEACHER.PATRONYMIC.sort(sortOrder);
        };

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
        if(Objects.nonNull(search)){
            condition = condition.and(TEACHER.NAME.containsIgnoreCase(search).or(TEACHER.SURNAME.containsIgnoreCase(search))
                    .or(TEACHER.PATRONYMIC.containsIgnoreCase(search)));
        }

        List<Teacher> items = teacherRepository.fetch(condition, page, pageSize, sortField);
        Long total = teacherRepository.getCount(condition);

        ResponseList<Teacher> result = new ResponseList<>();
        result.setItems(items);
        result.setTotal(total);

        return result;
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

        // Удаление текущего состояния сущности и добавление нового
        teacherRepository.updateDateTimeOfDeleteByCondition(condition, LocalDateTime.now());
        teacherDao.insert(teacher);
    }

    public void delete(Long id) {
        teacherRepository.updateDateTimeOfDeleteByIds(List.of(id), LocalDateTime.now());
    }
}
