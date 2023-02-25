package com.example.aupo.controller.group;

import com.example.aupo.tables.pojos.Group;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/grade")
@AllArgsConstructor
public class GroupRestController {

    @GetMapping(value = "/list")
    public List<Group> getList(){
        return null;
    }


}
