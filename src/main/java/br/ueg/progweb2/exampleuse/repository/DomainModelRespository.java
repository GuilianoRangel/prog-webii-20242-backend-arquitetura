package br.ueg.progweb2.exampleuse.repository;

import br.ueg.progweb2.exampleuse.model.DomainModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DomainModelRespository
        extends JpaRepository<DomainModel, Long> {
}
