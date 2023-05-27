package nl.esciencecenter.service;

import java.util.ArrayList;
import java.util.List;
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
        List<Domain> domains = new ArrayList<Domain>();
        domainRepository.findAll().forEach(domain -> domains.add(domain));
        return domains;
    }

    // getting a specific record
    public Domain getDomainById(int id) {
        return domainRepository.findById(id).get();
    }

    public void saveOrUpdate(Domain domain) {
        domainRepository.save(domain);
    }

    // deleting a specific record
    public void delete(int id) {
        domainRepository.deleteById(id);
    }
}
