package com.example.aupo.controller;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.service.TeacherService;
import com.example.aupo.dto.TeacherCreateDto;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.sort.TeacherSortEnum;
import com.example.aupo.tables.pojos.Teacher;
import com.example.aupo.util.LogUtil;
import lombok.AllArgsConstructor;
import org.jooq.SortOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/teacher")
public class TeacherRestController {

    private final TeacherService teacherService;

    /**
     * Просмотр отдельной сущности по внешнему идентификатору
     */
    @GetMapping(value = "/{entityId}")
    public ResponseEntity<Teacher> get(@PathVariable Long entityId) throws NotFoundException {
        try {
            return ResponseEntity.ok(teacherService.getOne(entityId));
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
            @RequestParam(value = "patronymic", required = false) String patronymic,
            @RequestParam(value = "search", required = false) String search
    ){
        return teacherService.getList(page, pageSize, name, surname, patronymic, teacherSortEnum, sortOrder, search);
    }

    @PostMapping
    public void create(@RequestBody TeacherCreateDto teacherCreateDto){
        teacherService.createTeacher(teacherCreateDto);
    }

    @PutMapping
    public void update(@RequestBody Teacher teacher){
        teacherService.updateTeacher(teacher);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teacherService.delete(id);
    }
}
