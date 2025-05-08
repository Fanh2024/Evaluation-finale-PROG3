package edu.hei.school.evaluation.controller;

import edu.hei.school.evaluation.model.Transfert;
import edu.hei.school.evaluation.repository.TransfertRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transfert")
public class TransfertController {

    private final TransfertRepository repository;

    public TransfertController(TransfertRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Transfert> getAllTransferts() {
        return repository.findAll();
    }
}
