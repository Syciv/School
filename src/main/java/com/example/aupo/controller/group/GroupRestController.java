package com.example.aupo.controller.group;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.exception.NotFoundException;
import com.example.aupo.tables.pojos.Group;
import com.example.aupo.util.LogUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/grade")
@AllArgsConstructor
public class GroupRestController {

    private final GroupRestService groupRestService;

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
    public void get(@RequestBody GroupCreateDto groupCreateDto){
        groupRestService.create(groupCreateDto);
    }

    @PutMapping
    public void get(@RequestBody Group group){
        groupRestService.update(group);
    }

}
