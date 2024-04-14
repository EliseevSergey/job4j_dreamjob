package ru.job4j.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.controller.UserController;
import ru.job4j.dreamjob.service.UserService;
import static org.mockito.Mockito.mock;

public class UserControllerTest {
    private UserService userService;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        UserController userController = new UserController(userService);
    }

    @Test
    public void whenNoRegisterThenRedirectToLogInPage() {


    }
}
