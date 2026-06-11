package ru.practicum.explore_with_me.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.hit.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class RequestController {
private final RequestClient requestClient;

    @PostMapping("/hit")
    public ResponseEntity<Object> addHit(@Valid @RequestBody HitDto hitDto) {
        return new ResponseEntity<>(requestClient.addHit(hitDto), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStat(@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")@RequestParam("start") LocalDateTime start,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("end") LocalDateTime end, @RequestParam(value = "uris",required = false) List<String> uris, @RequestParam(defaultValue = "false", name = "unique") boolean unique) {
        return new ResponseEntity<>(requestClient.getStat(start,end,uris,unique), HttpStatus.OK);
    }
}
