package ru.job4j.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.Sql2oUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void truncateAllUsers() {
        sql2oUserRepository.truncateAllUsers();
    }

    @Test
    public void whenSaveThenGetSame() {
        Optional<User> user = sql2oUserRepository.save(new User(6, "с112@gmail.com", "a", "аc"));
        Optional<User> savedUser = sql2oUserRepository.findByEmailAndPassword(user.get().getEmail(), user.get().getPassword());
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveSeveral() {
        User user1 = new User(1, "111@gmail.com", "a", "а");
        User user2 = new User(2, "222@gmail.com", "b", "а");
        User user3 = new User(3, "333@gmail.com", "c", "а");
        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);
        sql2oUserRepository.save(user3);
        List<User> exp = new ArrayList<>();
        exp.add((sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword())).get());
        exp.add((sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword())).get());
        exp.add((sql2oUserRepository.findByEmailAndPassword(user3.getEmail(), user3.getPassword())).get());
        assertThat(exp).isEqualTo(List.of(user1, user2, user3));
    }

    @Test
    public void whenSameEmailThenReject() {
        User user1 = new User(1, "111@gmail.com", "a", "а");
        User user2 = new User(2, "111@gmail.com", "b", "b");
        sql2oUserRepository.save(user1);
        assertThrows(org.sql2o.Sql2oException.class, () -> sql2oUserRepository.save(user2));
    }
}
