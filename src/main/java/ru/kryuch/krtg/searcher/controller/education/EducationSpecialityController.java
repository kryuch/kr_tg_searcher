package ru.kryuch.krtg.searcher.controller.education;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.dto.OptionDto;
import ru.kryuch.krtg.searcher.dto.education.EducationSpecialtyDto;
import ru.kryuch.krtg.searcher.dto.education.EducationUniversityDto;
import ru.kryuch.krtg.searcher.service.education.EducationSpecialtyService;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/education/speciality")
public class EducationSpecialityController extends TBaseController <EducationSpecialtyService> {

    public EducationSpecialityController(final EducationSpecialtyService service) {
        super(service, "education/speciality");
    }

    @GetMapping("/values")
    @ResponseBody
    public List<OptionDto> getValues() {
        return service.getAll().stream()
            .sorted(Comparator.comparing(EducationSpecialtyDto::getName, String.CASE_INSENSITIVE_ORDER))
            .map(u -> new OptionDto(u.getId(), u.getName()))
            .toList();
    }

    @PostMapping("/add")
    @ResponseBody
    public void addValue(@RequestParam String code, @RequestParam String name) {
      //  service.add(code, name);
    }
}
