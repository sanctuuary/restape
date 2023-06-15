package nl.esciencecenter.repository;

import org.springframework.data.repository.CrudRepository;

import nl.esciencecenter.models.Domain;

public interface DomainRepository extends CrudRepository<Domain, Integer> {

}
