/* 	 
 * UsuarioSenhaTO.java  
 * Copyright UEG.
 */
package br.ueg.progweb2.arquitetura.model.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Classe de transferência referente as alterações de senha do usuário.
 * 
 * @author UEG
 */

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Entidade de transferência de Sistema")
public @Data class AuthUserDTO implements Serializable {

	private static final long serialVersionUID = 3198994205885488802L;

	public enum PasswordResetType {
		activate, recover, change
	}

	@Schema(description = "E-mail do Usuário onde a solicitação de senha foi enviada. (Campo não será considerado como parâmetro de entrada)")
	private String email;

	@Size(min = 8, max = 20, message = "deve ser maior ou igual a 8 e menor ou igual a 20")
	@Schema(description = "Senha Antiga")
	private String oldPassword;

	@Schema(description = "Nova Senha")
	@Size(min = 8, max = 20, message = "deve ser maior ou igual a 8 e menor ou igual a 20")
	private String newPassword;

	@Schema(description = "Confirmar Senha")
	@Size(min = 8, max = 20, message = "deve ser maior ou igual a 8 e menor ou igual a 20")
	private String confirmPassword;

	@JsonIgnore
	@Schema(hidden = true)
	private Long userId;

	@JsonIgnore
	@Schema(hidden = true)
	private PasswordResetType type;

	/**
	 * @param email email do usuário
	 */
	public AuthUserDTO(String email){
		this.email = email;
	}

	/**
	 * Verifica se o Tipo é igual a 'ativacao'.
	 * 
	 * @return -
	 */
	@JsonIgnore
	@Schema(hidden = true)
	public boolean isActivate() {
		return PasswordResetType.activate.equals(type);
	}

	/**
	 * Verifica se o Tipo é igual a 'recuperacao'.
	 * 
	 * @return -
	 */
	@JsonIgnore
	@Schema(hidden = true)
	public boolean isRecover() {
		return PasswordResetType.recover.equals(type);
	}
}
