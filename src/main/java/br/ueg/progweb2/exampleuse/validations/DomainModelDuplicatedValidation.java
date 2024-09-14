package br.ueg.progweb2.exampleuse.validations;

import br.ueg.progweb2.arquitetura.exceptions.BusinessException;
import br.ueg.progweb2.arquitetura.validations.IValidations;
import br.ueg.progweb2.arquitetura.validations.ValidationAction;
import br.ueg.progweb2.exampleuse.exceptions.AppExampleMessageCode;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.repository.DomainModelRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Order(2)
@Component
public class DomainModelDuplicatedValidation implements IValidations<DomainModel> {
    @Autowired
    protected DomainModelRespository repository;
    @Override
    public void validate(DomainModel data, ValidationAction action) {
        if(Arrays.asList(
                ValidationAction.CREATE
        ).contains(action) ) {
            Optional<DomainModel> byRegisterNumber = repository.findById(data.getId());
            if (byRegisterNumber.isPresent()) {
                throw new BusinessException(AppExampleMessageCode.MSG_DOMAIN_MODEL_EXISTS);
            }
        }
    }
}
