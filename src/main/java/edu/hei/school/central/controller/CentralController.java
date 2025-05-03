/*
package edu.hei.school.central.controller;

import edu.hei.school.central.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CentralController {

    private final SyncService syncService;

    public CentralController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/synchronization")
    public ResponseEntity<String> synchronize() {
        syncService.synchronizeData();
        return ResponseEntity.ok("Synchronization complete.");
    }
}

 */