/*
 * Util.java
 * Copyright (c) UEG.
 */
package br.ueg.progweb2.arquitetura.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Classe utilitária padrão referente as aplicações UEG.
 * 
 * @author UEG
 */
public final class Util {

	/**
	 * Construtor privado para garantir o singleton.
	 */
	private Util() {

	}

	/**
	 * Verifica se o valor informado está vazio.
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(final String value) {
		return StringUtils.isEmpty(value);
	}

	/**
	 * Remove os caracteres não numéricos do 'valor' informado.
	 * 
	 * @param valor
	 * @return
	 */
	public static String removeNotNumericCaracters(String valor) {

		if (!Util.isEmpty(valor)) {
			valor = valor.replaceAll("[^\\d]", "");
		}
		return valor;
	}

	/**
	 * Retorna a string com os dados do array de objetos concatenados conforme o
	 * separador informado.
	 * 
	 * @param separador
	 * @param parametros
	 * @return
	 */
	public static String getConcatValues(final String separador, Object... parametros) {
		return Util.getConcatValues(separador, Arrays.asList(parametros));
	}

	/**
	 * Retorna a string com os dados da {@link List} concatenados conforme o
	 * separador informado.
	 * 
	 * @param separador
	 * @param parametros
	 * @return
	 */
	public static String getConcatValues(final String separador, final List<Object> parametros) {
		StringBuilder build = new StringBuilder();
		Iterator<?> iterator = parametros.iterator();

		while (iterator.hasNext()) {
			Object valor = iterator.next().toString();
			build.append(valor);

			if (iterator.hasNext()) {
				build.append(separador);
			}
		}
		return build.toString();
	}

	/**
	 * Retorna o map serializado no formato json.
	 * 
	 * @param data
	 * @return
	 */
	public static String toJson(final Map<String, Object> data) {
		String json = "";

		try {
			json = new ObjectMapper().writeValueAsString(data);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return json;
	}


	/**
	 * Verifica se o e-mail informado é válido.
	 *
	 * @param mail
	 * @return
	 */
	public static boolean isValidEmail(final String mail) {
		boolean valid = Boolean.TRUE;

		try {
			new InternetAddress(mail).validate();
		} catch (AddressException e) {
			valid = Boolean.FALSE;
		}
		return valid;
	}
}
