package com.example.board.user.api;

import com.example.board.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/createTemp")
    public String create() {
        userService.createTempUser();
        return "success";
    }
}
