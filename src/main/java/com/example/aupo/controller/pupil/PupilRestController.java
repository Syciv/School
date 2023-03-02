package com.example.aupo.controller.pupil;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.util.CSVUtil;
import com.example.aupo.util.LogUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/pupil")
@AllArgsConstructor
public class PupilRestController {

    private final PupilRestService pupilRestService;

    /**
     * Просмотр отдельной сущности по внешнему идентификатору
     */
    @GetMapping(value = "/{entityId}")
    public ResponseEntity<Pupil> get(@PathVariable Long entityId) {
        try {
            return ResponseEntity.ok(pupilRestService.getOne(entityId));
        } catch (NotFoundException e) {
            LogUtil.info(String.format("Не найдено: pupil - %d", entityId));
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Пагинированный список с фильтрами по всем полям
     */
    @GetMapping(value = "/list")
    public ResponseList<Pupil> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname,
            @RequestParam(value = "patronymic", required = false) String patronymic,
            @RequestParam(value = "groupId", required = false) Long groupId
            ){
        return pupilRestService.getList(page, pageSize, name, surname, patronymic, groupId);
    }

    @PostMapping
    public void create(@RequestBody PupilCreateDto pupilCreateDto){
        pupilRestService.createPupil(pupilCreateDto);
    }

    @PutMapping
    public void update(@RequestBody Pupil pupil){
        pupilRestService.updatePupil(pupil);
    }


    @PostMapping(value = "upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCsv(MultipartFile csvFile) {
        String fileContent;

        try {
            fileContent = CSVUtil.getFileContent(csvFile);
        }
        catch (IOException exception){
            LogUtil.info("Невозможно обработать файл pupil: " + csvFile.getName());
            return ResponseEntity.badRequest().body("Невозможно обработать файл");
        }

        pupilRestService.saveFromCSV(fileContent);
        return ResponseEntity.ok("Ок");
    }


    /**
     * Массовое перемещение учеников в группу
     */
    @PutMapping("/migrate")
    public void migrate(@RequestBody PupilMigrateDto pupilMigrateDto){
        pupilRestService.migrate(pupilMigrateDto.getPupilIds(), pupilMigrateDto.getGroupId());
    }


}
