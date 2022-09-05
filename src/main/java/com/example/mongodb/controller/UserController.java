package com.example.mongodb.controller;


import com.example.mongodb.controller.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/tokenInfo/{walletAddress}")
    public String tokenInfo(@PathVariable("walletAddress") String walletAddress){
        log.info(walletAddress);
        return userService.getTokenOwnershipWithWalletAddress(walletAddress).toString();

    }

    @GetMapping("/nftInfo/{walletAddress}")
    public String nftInfo(@PathVariable("walletAddress") String walletAddress){
        return userService.getNFTTokenOwnershipWithWalletAddress(walletAddress).toString();
    }

}
