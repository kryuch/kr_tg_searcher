package ru.kryuch.krtg.searcher.controller.development;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.kryuch.krtg.searcher.controller.base.TBaseController;
import ru.kryuch.krtg.searcher.dto.OptionDto;
import ru.kryuch.krtg.searcher.dto.developement.DevelopmentSkillDto;
import ru.kryuch.krtg.searcher.service.development.DevelopmentSkillService;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/development/Skills")
public class DevelopmentSkillController extends TBaseController<DevelopmentSkillService> {

    public DevelopmentSkillController(final DevelopmentSkillService service) {
        super(service, "development/skill");
    }

    @GetMapping("/values")
    @ResponseBody
    public List<OptionDto> getValues() {
        return service.getAll().stream()
            .sorted(Comparator.comparing(DevelopmentSkillDto::getName, String.CASE_INSENSITIVE_ORDER))
            .map(u -> new OptionDto(u.getId(), u.getName()))
            .toList();
    }

    @PostMapping("/add")
    @ResponseBody
    public void addValue(@RequestParam String code, @RequestParam String name) {
        //    service.add(code, name);
    }
}
