package br.ueg.progweb2.arquitetura.service;

import br.ueg.progweb2.arquitetura.model.GenericModel;

import java.util.List;

public interface CrudService<
        MODEL extends GenericModel<TYPE_PK>, TYPE_PK
        > {
    Class<TYPE_PK> getEntityType();

    List<MODEL> listAll();
    MODEL create(MODEL dado);
    MODEL update(MODEL dado);

    MODEL getById(TYPE_PK id);

    MODEL deleteById(TYPE_PK id);
}
