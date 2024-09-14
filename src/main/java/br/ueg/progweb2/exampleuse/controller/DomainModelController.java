package br.ueg.progweb2.exampleuse.controller;

import br.ueg.progweb2.arquitetura.controllers.GenericCRUDController;
import br.ueg.progweb2.exampleuse.mapper.DomainModelMapper;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.model.dtos.DomainModelDTO;
import br.ueg.progweb2.exampleuse.service.DomainModelService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "${app.api.base}/domain-model")
@RestController
public class DomainModelController  extends GenericCRUDController<
        DomainModelDTO,
        DomainModelDTO,
        DomainModelDTO,
        DomainModelDTO,
        DomainModel,
        Long,
        DomainModelService,
        DomainModelMapper
        > {

    }

