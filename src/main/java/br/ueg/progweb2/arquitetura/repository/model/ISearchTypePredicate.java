package br.ueg.progweb2.arquitetura.repository.model;

import br.ueg.progweb2.arquitetura.model.dtos.SearchFieldValue;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface ISearchTypePredicate {
    Predicate execute(Root<?> root, CriteriaBuilder cb, SearchFieldValue searchFieldValue);
}
