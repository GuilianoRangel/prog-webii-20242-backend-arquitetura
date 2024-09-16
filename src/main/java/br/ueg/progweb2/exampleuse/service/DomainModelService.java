package br.ueg.progweb2.exampleuse.service;

import br.ueg.progweb2.arquitetura.service.CrudService;
import br.ueg.progweb2.exampleuse.model.DomainModel;

import java.util.List;

public interface DomainModelService
        extends CrudService<DomainModel, Long> {

    List<DomainModel> listAllActives();

    Long removeAllInatives();
}
