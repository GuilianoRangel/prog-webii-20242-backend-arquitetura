package br.ueg.progweb2.arquitetura.service;

import br.ueg.progweb2.arquitetura.model.GenericModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrudService<
        MODEL extends GenericModel<TYPE_PK>, TYPE_PK
        > {
    Class<TYPE_PK> getEntityType();

    List<MODEL> listAll();
    Page<MODEL> listAllPage(Pageable page);
    MODEL create(MODEL dado);
    MODEL update(MODEL dado);

    MODEL getById(TYPE_PK id);

    MODEL deleteById(TYPE_PK id);
}
