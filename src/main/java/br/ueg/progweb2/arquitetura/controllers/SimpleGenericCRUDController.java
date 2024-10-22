package br.ueg.progweb2.arquitetura.controllers;

import br.ueg.progweb2.arquitetura.mapper.GenericMapper;
import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.service.CrudService;

public abstract class SimpleGenericCRUDController<
        DTO,
        MODEL extends GenericModel<TYPE_PK>,
        TYPE_PK,
        SERVICE extends CrudService<MODEL, TYPE_PK>,
        MAPPER extends GenericMapper<DTO, DTO, DTO, DTO , MODEL, TYPE_PK>
        > extends GenericCRUDController<DTO, DTO, DTO, DTO, MODEL, TYPE_PK, SERVICE, MAPPER>
        {
}
