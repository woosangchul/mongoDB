package com.example.mongodb.controller;


import com.example.mongodb.controller.dto.ResultDTO;
import com.example.mongodb.controller.service.UserService;
import com.example.mongodb.entity.StakingInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user/")
@Log4j2
public class UserController {

    private final UserService userService;

    @GetMapping("/getUser1")
    public String getUser(){
        log.info("test");
        return userService.getTokenOwnership().toString();

    }

}
