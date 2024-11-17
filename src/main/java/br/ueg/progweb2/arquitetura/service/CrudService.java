package br.ueg.progweb2.arquitetura.service;

import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.model.dtos.SearchField;
import br.ueg.progweb2.arquitetura.model.dtos.SearchFieldValue;
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

    /**
     * Retorna uma lista dos campos utilizáveis para busca no controlador
     * @return lista de campos
     */
    List<SearchField> listSearchFields();

    /**
     * Método para realizar a busca utilizando o componete de busca.
     * @param searchFieldValues - lista de campos para fazer a busca
     * @return lista de modelos retornados.
     */
    List<MODEL> searchFieldValues(List<SearchFieldValue> searchFieldValues);

    /**
     * Métod para realizar a busca utilizando o componete de busca com paginação backend
     * @param page - dados da paginação
     * @param searchFieldValues - lista de campos para fazer a busca
     * @return lista de modelos retornados na busca
     */
    Page<MODEL> searchFieldValuesPage(Pageable page, List<SearchFieldValue> searchFieldValues);
}
