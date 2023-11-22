package nl.esciencecenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.esciencecenter.models.Domain;
import nl.esciencecenter.repository.DomainRepository;

@Service
public class DomainService {

    @Autowired
    DomainRepository domainRepository;

    // getting all domain records
    public List<Domain> getAllDomain() {
        List<Domain> domains = new ArrayList<>();
        domainRepository.findAll().forEach(domains::add);
        return domains;
    }

    /**
     * Get the domain object with the given id.
     * 
     * @param id - id of the domain
     * @return - Domain object with the given id
     * @throws NoSuchElementException - if no domain with the given id exists
     */
    public Domain getDomainById(int id) {
        Optional<Domain> optionalDomain = domainRepository.findById(id);
        return optionalDomain.orElseThrow(() -> new NoSuchElementException("No domain found with id: " + id));
    }

    public void saveOrUpdate(Domain domain) {
        domainRepository.save(domain);
    }

    // deleting a specific record
    public void delete(int id) {
        domainRepository.deleteById(id);
    }
}
