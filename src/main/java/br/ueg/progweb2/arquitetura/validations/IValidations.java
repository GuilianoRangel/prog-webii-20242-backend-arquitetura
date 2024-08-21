package br.ueg.progweb2.arquitetura.validations;

public interface IValidations<MODEL> {
    void validate(MODEL data, ValidationAction action);
    //Sugestão para determinar para quais momentos fazer a validação
    //List<ValidationAction> getActions();
}
