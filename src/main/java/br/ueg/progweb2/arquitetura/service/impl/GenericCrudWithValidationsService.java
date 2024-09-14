package br.ueg.progweb2.arquitetura.service.impl;

import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.validations.IValidations;
import br.ueg.progweb2.arquitetura.validations.ValidationAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class GenericCrudWithValidationsService<
        MODEL extends GenericModel<TYPE_PK>,
        TYPE_PK,
        REPOSITORY extends JpaRepository<MODEL, TYPE_PK>
        > extends GenericCrudService<MODEL, TYPE_PK, REPOSITORY> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired(required = false)
    private List<IValidations<MODEL>> validations;

    protected void validateBusinessLogicForInsert(MODEL data) {
        if(validations != null && !validations.isEmpty()) {
            validations.forEach(v -> v.validate(data, ValidationAction.CREATE));
        }
    }

    protected  void validateBusinessLogicForUpdate(MODEL data) {
        if(validations != null && !validations.isEmpty()) {
            validations.forEach(v -> v.validate(data, ValidationAction.UPDATE));
        }
    }

    protected void validateBusinessLogic(MODEL data) {
        if(validations != null && !validations.isEmpty()) {
            validations.forEach(v -> v.validate(data, ValidationAction.GENERAL));
        }
    }

}
