package br.ueg.progweb2.exampleuse.service.impl;

import br.ueg.progweb2.arquitetura.mapper.GenericUpdateMapper;
import br.ueg.progweb2.arquitetura.service.impl.GenericCrudService;
import br.ueg.progweb2.arquitetura.service.impl.GenericCrudWithValidationsService;
import br.ueg.progweb2.exampleuse.mapper.DomainModelMapper;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.repository.DomainModelRespository;
import br.ueg.progweb2.exampleuse.service.DomainModelService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DomainModelServiceImpl
        extends GenericCrudWithValidationsService<DomainModel, Long, DomainModelRespository>
        implements DomainModelService {
    @Override
    protected void prepareToCreate(DomainModel dado) {
        if(Objects.nonNull(dado.getDescription())) {
            dado.setDescription(dado.getDescription().toUpperCase());
        }
    }
}
