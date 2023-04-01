package com.example.aupo.controller.user;

import com.example.aupo.controller.dto.ResponseList;
import com.example.aupo.tables.pojos.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserRestController {

    private final UserRestService userRestService;

    @GetMapping("/list")
    public ResponseList<User> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ){
        return userRestService.list(page, pageSize);
    }

    @PostMapping
    public void create(@RequestBody UserCreateDto userCreateDto){
        userRestService.create(userCreateDto);
    }

    @PutMapping
    public void update(@RequestBody UserCreateDto userCreateDto){
        userRestService.update(userCreateDto);
    }

}
