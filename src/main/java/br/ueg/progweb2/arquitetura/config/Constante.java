/*
 * Constante.java
 * Copyright (c) UEG.
 */
package br.ueg.progweb2.arquitetura.config;

/**
 * Classe responsável por manter as constantes da aplicação.
 * 
 * @author UEG
 */
public final class Constante {

	/** JWT - Security */
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_NAME = "nome";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_LOGIN = "login";
	public static final String PARAM_ROLES = "roles";
	public static final String PARAM_USER_ID = "userId";
	public static final String PARAM_EXPIRES_IN = "expiresIn";
	public static final String PARAM_REFRESH_EXPIRES_IN = "refreshExpiresIn";
	public static final String PARAM_RESET_PASSWORD_TYPE = "passwordResetType";

	/** Authorization */
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_AUTHORIZATION_BEARER = "Bearer ";

	/**
	 * Construtor privado para garantir o singleton.
	 */
	private Constante() {
	}
}
