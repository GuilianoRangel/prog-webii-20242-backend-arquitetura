package br.ueg.progweb2.exampleuse.service.impl;

import br.ueg.progweb2.arquitetura.mapper.GenericUpdateMapper;
import br.ueg.progweb2.arquitetura.service.impl.GenericCrudService;
import br.ueg.progweb2.exampleuse.mapper.DomainModelMapper;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.repository.DomainModelRespository;
import br.ueg.progweb2.exampleuse.service.DomainModelService;
import org.springframework.stereotype.Service;

@Service
public class DomainModelServiceImpl
        extends GenericCrudService<DomainModel, Long, DomainModelRespository>
        implements DomainModelService {
    @Override
    protected void prepareToCreate(DomainModel dado) {
        dado.setDescription(dado.getDescription().toUpperCase());
    }

    @Override
    protected void validateBusinessLogicForInsert(DomainModel dado) {

    }

    @Override
    protected void validateBusinessLogicForUpdate(DomainModel dado) {

    }


    protected void validateBusinessLogic(DomainModel dado) {

    }
}
