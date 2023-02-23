package com.example.aupo.controller.pupil;

import com.example.aupo.tables.pojos.Pupil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
@RequestMapping(value = "/pupil")
@AllArgsConstructor
public class PupilRestController {

    private final PupilRestService pupilRestService;

    @GetMapping(value = "/list")
    public List<Pupil> list(
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

    @PostMapping(value = "upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCsv(MultipartFile csvFile) throws UnsupportedEncodingException {
        byte[] bytes;
        try {
            bytes = csvFile.getBytes();
        }
        catch (IOException exception){
            return ResponseEntity.badRequest().body("Невозможно обработать файл");
        }
        String fileContent = new String(bytes, "cp1251");
        pupilRestService.saveFromCSV(fileContent);
        return ResponseEntity.ok("Ок");
    }



}
