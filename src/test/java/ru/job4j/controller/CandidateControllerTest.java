package ru.job4j.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.controller.CandidateController;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CandidateControllerTest {
    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void init() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("test.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestCandidateList() {
        Candidate candidate1 = new Candidate(1, "Petrov", "Descr1", now(), 1, 1);
        Candidate candidate2 = new Candidate(2, "Ivanov", "Descr2", now(), 2, 2);
        Collection<Candidate> expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.getAll(model);
        Object actualCandidates = model.getAttribute("candidates");
        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");
        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileAndRedirectToCandidatePage() throws IOException {
        Candidate candidate1 = new Candidate(1, "Petrov", "Descr1", now(), 1, 1);
        FileDto fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate1);
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.create(candidate1, testFile, model);
        Object actualCandidate = candidateArgumentCaptor.getValue();
        Object actualFile = fileDtoArgumentCaptor.getValue();
        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate1);
        assertThat(actualFile).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenCandidateGetById() {
        Candidate candidate1 = new Candidate(1, "Petrov", "Descr1", now(), 1, 1);
        when(candidateService.findById(1)).thenReturn(Optional.of(candidate1));
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.getById(model, 1);
        Object actualCandidate = model.getAttribute("candidate");
        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate1);
    }

    @Test
    public void whenUpdateThenRedirectToCandidatesPage() throws IOException {
        Candidate candidateOld = new Candidate(1, "OldName", "OldDescr", now(), 1, 1);
        MultipartFile testFileNew = new MockMultipartFile("testNew.img", new byte[] {1, 2, 3});
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.update(candidateOld, testFileNew, model);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteThenRedirectToCandidatesPage() throws IOException {
        when(candidateService.deleteById(1)).thenReturn(true);
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.delete(model, 1);
        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteAbsentThenThrow() {
        var expectedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        ConcurrentModel model = new ConcurrentModel();
        String view = candidateController.delete(model, 1);
        var actualExceptionMessage = model.getAttribute("message");
        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }
}
