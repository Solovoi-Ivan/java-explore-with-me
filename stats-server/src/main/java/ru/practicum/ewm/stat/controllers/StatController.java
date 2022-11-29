package ru.practicum.ewm.stat.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stat.dto.EndpointHit;
import ru.practicum.ewm.stat.dto.ViewStats;
import ru.practicum.ewm.stat.services.StatService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping(path = "/hit")
    public void addEndpointHit(@Valid @RequestBody EndpointHit endpointHit) {
        log.info("Обработан POST-запрос /hit");
        statService.addEndpointHit(endpointHit);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start") String start,
                                    @RequestParam(name = "end") String end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Обработан GET-запрос /stats");
        return statService.getStats(start, end, uris, unique);
    }
}
