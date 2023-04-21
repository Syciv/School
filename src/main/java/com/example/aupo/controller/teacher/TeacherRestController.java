package com.example.aupo.controller.teacher;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.tables.pojos.Teacher;
import com.example.aupo.util.CSVUtil;
import com.example.aupo.util.LogUtil;
import lombok.AllArgsConstructor;
import org.jooq.SortOrder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/teacher")
public class TeacherRestController {

    private final TeacherRestService teacherRestService;

    /**
     * Просмотр отдельной сущности по внешнему идентификатору
     */
    @GetMapping(value = "/{entityId}")
    public ResponseEntity<Teacher> get(@PathVariable Long entityId) throws NotFoundException {
        try {
            return ResponseEntity.ok(teacherRestService.getOne(entityId));
        }
        catch (NotFoundException e){
            LogUtil.info(String.format("Не найдено: teacher - %d", entityId));
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Пагинированный список с фильтрами по всем полям
     */
    @GetMapping(value = "/list")
    public ResponseList<Teacher> list(
            @RequestParam(value = "sortBy", defaultValue = "ID") TeacherSortEnum teacherSortEnum,
            @RequestParam(value = "sortOrder", defaultValue = "ASC") SortOrder sortOrder,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "patronymic", required = false) String patronymic
    ){
        return teacherRestService.getList(page, pageSize, name, surname, patronymic, teacherSortEnum, sortOrder);
    }

    @PostMapping
    public void create(@RequestBody TeacherCreateDto teacherCreateDto){
        teacherRestService.createTeacher(teacherCreateDto);
    }

    @PutMapping
    public void update(@RequestBody Teacher teacher){
        teacherRestService.updateTeacher(teacher);
    }


}
