package nl.esciencecenter.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import nl.esciencecenter.models.SynthesisRun;
import nl.esciencecenter.service.SynthesisRunService;

//creating RestController 
public class SynthesisRunController {

    // autowired the SynthesisRunService class
    @Autowired
    SynthesisRunService synthesisRunService;

    // creating a get mapping that retrieves all the synthesisRuns detail from the
    // database
    @GetMapping("/synthesisRun")
    private List<SynthesisRun> getAllSynthesisRun() {
        return synthesisRunService.getAllSynthesisRun();
    }

    // creating a get mapping that retrieves the detail of a specific synthesisRun
    @GetMapping("/synthesisRun/{id}")
    private SynthesisRun getSynthesisRun(@PathVariable("id") int id) {
        return synthesisRunService.getSynthesisRunById(id);
    }

    // creating a delete mapping that deletes a specific synthesisRun
    @DeleteMapping("/synthesisRun/{id}")
    private void deleteSynthesisRun(@PathVariable("id") int id) {
        synthesisRunService.delete(id);
    }

    // creating post mapping that post the synthesisRun detail in the database
    @PostMapping("/synthesisRun")
    private int saveSynthesisRun(@RequestBody SynthesisRun synthesisRun) {
        synthesisRunService.saveOrUpdate(synthesisRun);
        return synthesisRun.getId();
    }
}
