package com.example.aupo.controller.pupil;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.tables.pojos.Pupil;
import com.example.aupo.util.CSVUtil;
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

    @GetMapping(value = "/{entityId}")
    public Pupil get(@PathVariable Long entityId){
        return pupilRestService.getOne(entityId);
    }

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
            return ResponseEntity.badRequest().body("Невозможно обработать файл");
        }
        pupilRestService.saveFromCSV(fileContent);
        return ResponseEntity.ok("Ок");
    }



}
