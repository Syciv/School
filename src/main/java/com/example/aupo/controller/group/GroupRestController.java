package com.example.aupo.controller.group;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/grade")
public class GroupRestController {

    @GetMapping(value = "/list")
    public List<GroupDto> getList(){
        return null;
    }


}
