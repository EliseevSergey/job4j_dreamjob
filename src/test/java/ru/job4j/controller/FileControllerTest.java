package ru.job4j.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.controller.FileController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileControllerTest {

    @Test
    public void whenGetById() {
        FileService fileService = mock(FileService.class);
        FileController fileController = new FileController(fileService);
        FileDto fileDto = new FileDto("test", new byte[] {1, 2, 3});
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));
        ResponseEntity<?> responseExpected = new ResponseEntity<>(fileDto.getContent(), HttpStatus.OK);
        ResponseEntity<?> actualResponse = fileController.getById(1);
        assertThat(actualResponse).isEqualTo(responseExpected);
    }
}
