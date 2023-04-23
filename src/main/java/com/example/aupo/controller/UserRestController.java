package com.example.aupo.controller;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.dto.UserCreateDto;
import com.example.aupo.service.UserService;
import com.example.aupo.tables.pojos.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseList<User> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ){
        return userService.list(page, pageSize);
    }

    @PostMapping
    public void create(@RequestBody UserCreateDto userCreateDto){
        userService.create(userCreateDto);
    }

    @PutMapping
    public void update(@RequestBody UserCreateDto userCreateDto){
        userService.update(userCreateDto);
    }

}
