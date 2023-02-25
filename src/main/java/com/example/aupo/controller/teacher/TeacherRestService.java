package com.example.aupo.controller.teacher;

import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.tables.pojos.Teacher;
import lombok.AllArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.example.aupo.tables.Pupil.PUPIL;
import static com.example.aupo.tables.Teacher.TEACHER;

@Service
@AllArgsConstructor
public class TeacherRestService {

    private final TeacherRepository teacherRepository;

    public List<Teacher> getList(
            Integer page,
            Integer pageSize,
            String name,
            String surname,
            String patronymic,
            Long groupId
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
        return teacherRepository.fetch(condition, page, pageSize);
    }


    public void createTeacher(TeacherCreateDto teacherCreateDto) {
    }

    public void saveFromCSV(String fileContent) {
    }
}
