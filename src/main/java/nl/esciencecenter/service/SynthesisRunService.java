package nl.esciencecenter.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.esciencecenter.models.SynthesisRun;
import nl.esciencecenter.repository.SynthesisRunRepository;

@Service
public class SynthesisRunService {

    @Autowired
    SynthesisRunRepository synthesisRunRepository;

    // getting all synthesisRun records
    public List<SynthesisRun> getAllSynthesisRun() {
        List<SynthesisRun> synthesisRuns = new ArrayList<SynthesisRun>();
        synthesisRunRepository.findAll().forEach(synthesisRun -> synthesisRuns.add(synthesisRun));
        return synthesisRuns;
    }

    // getting a specific record
    public SynthesisRun getSynthesisRunById(int id) {
        return synthesisRunRepository.findById(id).get();
    }

    public void saveOrUpdate(SynthesisRun synthesisRun) {
        synthesisRunRepository.save(synthesisRun);
    }

    // deleting a specific record
    public void delete(int id) {
        synthesisRunRepository.deleteById(id);
    }
}
