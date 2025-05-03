package edu.hei.school.central.controller;

import edu.hei.school.central.model.PlayerRanking;
import edu.hei.school.central.model.SynchronizationResult;
import edu.hei.school.central.service.SynchronizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;

@RestController
public class SynchronizationController {

    private final SynchronizationService synchronizationService;

    public SynchronizationController(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @PostMapping("/synchronization")
    public ResponseEntity<SynchronizationResult> synchronize() {
        SynchronizationResult result = synchronizationService.synchronize();
        return ResponseEntity.ok(result);
    }

}
