package br.ueg.progweb2.arquitetura.controllers.enums;

import lombok.Getter;

@Getter
public enum CrudSecurityRole implements ISecurityRole {
    CREATE  ("CREATE"   , "Incluir"),
    READ    ("READ"     , "Visualizar"),
    UPDATE  ("UPDATE"   , "Alterar"),
    DELETE  ("DELETE"   , "Remover"),
    READ_ALL("READ_ALL" , "Listar todos")
    ;
    private final String name;

    private final String label;

    CrudSecurityRole(final String name, final String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public String toString() {
        return name;
    }
}

