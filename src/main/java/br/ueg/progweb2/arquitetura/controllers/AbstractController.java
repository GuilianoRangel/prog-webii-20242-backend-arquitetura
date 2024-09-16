/*
 * AbstractController.java  
 * Copyright UEG.
 */
package br.ueg.progweb2.arquitetura.controllers;

import br.ueg.progweb2.arquitetura.model.dtos.CredencialDTO;
import br.ueg.progweb2.arquitetura.security.CredentialProvider;
import br.ueg.progweb2.arquitetura.service.IUserProviderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract Controller.
 * 
 * @author UEG
 */
public abstract class AbstractController {

	@Autowired
	protected IUserProviderService userProviderService;

	/**
	 * @return
	 */
	protected CredencialDTO getCredential() {
		return CredentialProvider.newInstance().getCurrentInstance(CredencialDTO.class);
	}

	/**
	 * @return the idUsuarioLogado
	 */
	protected Long getIdFromLoggedUser() {
		CredencialDTO credential = getCredential();
		return credential != null ? credential.getId() : null;
	}

	protected String getUserNameFromLoggedUser() {
		CredencialDTO credential = getCredential();
		return credential != null ? credential.getLogin() : null;
	}


	/**
	 * @return Retorna a instância do {@link CredencialDTO} logado.
	 */
	protected CredencialDTO getCredentialFromLoggedUser() {
		CredencialDTO credencialDTO = null;
		String login = getUserNameFromLoggedUser();

		if (login != null) {
			credencialDTO = userProviderService.getCredentialByLogin(login);
		}
		return credencialDTO;
	}

}
