package br.ueg.progweb2.arquitetura.service;

import br.ueg.progweb2.arquitetura.model.dtos.CredencialDTO;
import br.ueg.progweb2.arquitetura.model.dtos.AuthUserDTO;

public interface IUserProviderService {
    CredencialDTO getCredentialByLogin(String username);
    CredencialDTO resetPassword(AuthUserDTO authUserDTO);
    CredencialDTO getCredentialByEmail(String email);
}
