package ru.job4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.controller.UserController;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void init() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRegisterThenRedirectToLogInPage() {
        User user = new User(1, "mail@mail.ru", "The first", "password");
        when(userService.save(user)).thenReturn(Optional.of(user));
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.register(model, user);
        assertThat(view).isEqualTo("redirect:/users/login");
    }

    @Test
    public void whenRegisterFailThenError() {
        RuntimeException expectedException = new RuntimeException("Пользователь с такой почтой уже существует");
        User user = new User(1, "mail@mail.ru", "The first", "password");
        when(userService.save(any())).thenReturn(Optional.empty());
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.register(model, user);
        Object actualExceptionalMsg = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionalMsg).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenLoginUserThenRedirectToVacancies() {
        User user = new User(1, "mail@mail.ru", "The first", "password");
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.of(user));
        HttpServletRequest request =  mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        ConcurrentModel model = new ConcurrentModel();
        String view = userController.loginUser(user, model, request);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLogoutThenRedirectToLoginPage() {
        HttpSession session = mock(HttpSession.class);
        String view = userController.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}
