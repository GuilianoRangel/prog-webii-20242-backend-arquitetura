/*
 * SistemaMessageCode.java
 * Copyright (c) UEG.
 */
package br.ueg.progweb2.exampleuse.exceptions;

import br.ueg.progweb2.arquitetura.exceptions.MessageCode;

/**
 * Enum com os código de exceções/mensagens de negócio.
 * 
 * @author UEG S/A.
 */
public enum AppExampleMessageCode implements MessageCode {
	MSG_DOMAIN_MODEL_EXISTS("MSG-001", 400);
	;

	private final String code;

	private final Integer status;

	/**
	 * Construtor da classe.
	 *
	 * @param code -
	 * @param status -
	 */
	AppExampleMessageCode(final String code, final Integer status) {
		this.code = code;
		this.status = status;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @see Enum#toString()
	 */
	@Override
	public String toString() {
		return code;
	}
}
