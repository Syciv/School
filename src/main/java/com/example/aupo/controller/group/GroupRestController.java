package com.example.aupo.controller.group;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.tables.pojos.Group;
import com.example.aupo.util.CSVUtil;
import com.example.aupo.util.LogUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/group")
@AllArgsConstructor
public class GroupRestController {

    private final GroupRestService groupRestService;

    /**
     * Пагинированный список с фильтрами по всем полям
     */
    @GetMapping(value = "/list")
    public ResponseList<Group> getList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "parallelEntityId", required = false) Long parallelEntityId,
            @RequestParam(value = "teacherEntityId", required = false) Long teacherEntityId,
            @RequestParam(value = "year", required = false) Integer year
    ){
        return groupRestService.list(
                page,
                pageSize,
                parallelEntityId,
                teacherEntityId,
                year
        );
    }

    /**
     * Просмотр отдельной сущности по внешнему идентификатору
     */
    @GetMapping(value = "/{entityId}")
    public ResponseEntity<Group> get(@PathVariable Long entityId){
        try {
            return ResponseEntity.ok(groupRestService.getOne(entityId));
        } catch (NotFoundException e) {
            LogUtil.info(String.format("Не найдено: group - %d", entityId));
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public void create(@RequestBody GroupCreateDto groupCreateDto){
        groupRestService.create(groupCreateDto);
    }

    @PutMapping
    public void get(@RequestBody Group group){
        groupRestService.update(group);
    }

    @PostMapping(value = "upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCsv(MultipartFile csvFile) {
        String fileContent;

        try {
            fileContent = CSVUtil.getFileContent(csvFile);
        }
        catch (IOException exception){
            LogUtil.info("Невозможно обработать файл group: " + csvFile.getName());
            return ResponseEntity.badRequest().body("Невозможно обработать файл");
        }

        groupRestService.saveFromCSV(fileContent);
        return ResponseEntity.ok("Ок");
    }

}
