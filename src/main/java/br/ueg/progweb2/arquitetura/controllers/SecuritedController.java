package br.ueg.progweb2.arquitetura.controllers;

import br.ueg.progweb2.arquitetura.controllers.enums.ISecurityRole;

import java.util.List;

public interface SecuritedController {
    String getRoleName(ISecurityRole role);

    String getEntityTypeSimpleName();

    String getSecurityModuleName();

    String getSecurityModuleLabel();

    List<ISecurityRole> getSecurityModuleFeatures();
}
