package nl.esciencecenter.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import nl.esciencecenter.models.Domain;
import nl.esciencecenter.service.DomainService;

/**
 * RestController to handle the Domain data from the database.
 * Not yet implemented.
 */
public class NotYetAController {

    // autowired the DomainService class
    @Autowired
    DomainService domainService;

    // creating a get mapping that retrieves all the synthesisRuns detail from the
    // database
    @GetMapping("/domain")
    private List<Domain> getAllDomain() {
        return domainService.getAllDomain();
    }

    // creating a get mapping that retrieves the detail of a specific synthesisRun
    @GetMapping("/domain/{id}")
    private Domain getSynthesisRun(@PathVariable("id") int id) {
        return domainService.getDomainById(id);
    }

    // creating a delete mapping that deletes a specific synthesisRun
    @DeleteMapping("/domain/{id}")
    private void deleteDomain(@PathVariable("id") int id) {
        domainService.delete(id);
    }

    // creating post mapping that post the synthesisRun detail in the database
    @PostMapping("/domain")
    private int saveSynthesisRun(@RequestBody Domain domain) {
        domainService.saveOrUpdate(domain);
        return domain.getId();
    }
}
