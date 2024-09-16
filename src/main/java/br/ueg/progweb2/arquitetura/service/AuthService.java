/*
 * AuthService.java
 * Copyright (c) UEG.
 */
package br.ueg.progweb2.arquitetura.service;


import br.ueg.progweb2.arquitetura.config.Constante;
import br.ueg.progweb2.arquitetura.exceptions.ApiMessageCode;
import br.ueg.progweb2.arquitetura.exceptions.BusinessException;
import br.ueg.progweb2.arquitetura.model.dtos.AuthDTO;
import br.ueg.progweb2.arquitetura.model.dtos.CredencialDTO;
import br.ueg.progweb2.arquitetura.model.dtos.AuthUserDTO;
import br.ueg.progweb2.arquitetura.security.KeyToken;
import br.ueg.progweb2.arquitetura.security.TokenBuilder;
import com.auth0.jwt.interfaces.Claim;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class de serviço responsável por prover as logicas de negócio referente a
 * Autenticação/Autorização.
 *
 * @author UEG
 */
@Component
public class AuthService {

    @Autowired
    private KeyToken keyToken;

    @Autowired
    private IUserProviderService userProviderService;

    @Value("${app.api.security.jwt.token-expire-in:600}")
    private Long tokenExpireIn;

    @Value("${app.api.security.jwt.token-refresh-in:600}")
    private Long tokenRefreshExpireIn;

    /**
     * Autentica o Usuário concede um token de acesso temporário.
     *
     * @param authDTO -
     * @return -
     */
    public CredencialDTO login(final AuthDTO authDTO) {
        return loginAccess(authDTO);
    }

    public static Boolean loginByPassword(CredencialDTO usuario, AuthDTO authDTO) {
        return UserPasswordService.loginByPassword(usuario, authDTO);
    }

    /**
     * Autentica o Usuário informado através do 'login' e 'senha' e concede um token
     * de acesso temporário.
     *
     * @param authDTO -
     * @return -
     */
    public CredencialDTO loginAccess(final AuthDTO authDTO) {
        CredencialDTO credencialDTO = null;

        validateMandatoryFieldsOnLoginAccess(authDTO);

        CredencialDTO userCredential = userProviderService.getCredentialByLogin(authDTO.getLogin());
        validateLoginUser(userCredential);

        if (!loginByPassword(userCredential, authDTO)) {
            throw new BusinessException(ApiMessageCode.ERROR_USER_PASSWORD_NOT_MATCH);
        }

        credencialDTO = userCredential;

        TokenBuilder builder = new TokenBuilder(keyToken);
        builder.addName(userCredential.getName());
        builder.addLogin(userCredential.getLogin());
        builder.addParam(Constante.PARAM_EMAIL, userCredential.getEmail());
        builder.addParam(Constante.PARAM_USER_ID, userCredential.getId());
        builder.addParam(Constante.PARAM_EXPIRES_IN, tokenExpireIn);
        builder.addParam(Constante.PARAM_REFRESH_EXPIRES_IN, tokenRefreshExpireIn);

        List<String> roles = null;

        roles = userCredential.getRoles();

        TokenBuilder.JWTToken accessToken = builder.buildAccess(tokenExpireIn);
        credencialDTO.setExpiresIn(accessToken.getExpiresIn());
        credencialDTO.setAccessToken(accessToken.getToken());

        TokenBuilder.JWTToken refreshToken = builder.buildRefresh(tokenRefreshExpireIn);
        credencialDTO.setRefreshExpiresIn(refreshToken.getExpiresIn());
        credencialDTO.setRefreshToken(refreshToken.getToken());
        credencialDTO.setRoles(roles);


        registerCredentialInSecurityContext(credencialDTO);
        credencialDTO.setPassword(null);

        return credencialDTO;
    }

    /**
     * Registra a credencial que acabou de fazer login no Contexto de segurança
     * Motivação: criado para poder registrar auditoria das alterações realizadas na entidade
     * de usuários durante o login.
     *
     * @param credencialDTO -
     */
    private void registerCredentialInSecurityContext(CredencialDTO credencialDTO) {
        //Cria instancia da autenticação para ter informações para a auditoria
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(credencialDTO.getLogin(), credencialDTO);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * Gera um novo token de acesso atráves do refresh token informado.
     *
     * @param refreshToken -
     * @return -
     */
    public CredencialDTO refresh(final String refreshToken) {
        AuthClaimResolve resolve = getClaimResolve(refreshToken);
        TokenBuilder builder = new TokenBuilder(keyToken);

        if (!resolve.isTokenTypeRefresh()) {
            throw new BusinessException(ApiMessageCode.ERROR_INVALID_TOKEN);
        }

        List<String> roles = null;
        CredencialDTO credencialDTO = userProviderService.getCredentialByLogin(resolve.getLogin());


        roles = Objects.nonNull(credencialDTO) ? credencialDTO.getRoles() : Arrays.asList();


        credencialDTO.setName(resolve.getName());
        credencialDTO.setEmail(resolve.getEmail());
        credencialDTO.setLogin(resolve.getLogin());
        credencialDTO.setId(resolve.getUserId());

        if (resolve.getUserId() != null) {
            builder.addName(resolve.getName());
            builder.addLogin(resolve.getLogin());
            builder.addParam(Constante.PARAM_EMAIL, resolve.getEmail());
            builder.addParam(Constante.PARAM_USER_ID, resolve.getUserId());
        }

        Long expiresIn = resolve.getExpiresIn();
        builder.addParam(Constante.PARAM_EXPIRES_IN, expiresIn);

        Long refreshExpiresIn = resolve.getRefreshExpiresIn();
        builder.addParam(Constante.PARAM_REFRESH_EXPIRES_IN, refreshExpiresIn);

        TokenBuilder.JWTToken accessToken = builder.buildAccess(expiresIn);
        credencialDTO.setExpiresIn(accessToken.getExpiresIn());
        credencialDTO.setAccessToken(accessToken.getToken());

        TokenBuilder.JWTToken newRefreshToken = builder.buildRefresh(refreshExpiresIn);
        credencialDTO.setRefreshExpiresIn(newRefreshToken.getExpiresIn());
        credencialDTO.setRefreshToken(newRefreshToken.getToken());
        credencialDTO.setRoles(roles);
        return credencialDTO;
    }

    /**
     * Retorna as informações do {@link CredencialDTO} conforme o 'token' informado.
     *
     * @param token -
     * @return -
     */
    public CredencialDTO getInfoByToken(final String token) {
        AuthClaimResolve resolve = getClaimResolve(token);

        if (!resolve.isTokenTypeAccess()) {
            throw new BusinessException(ApiMessageCode.ERROR_INVALID_TOKEN);
        }

        List<String> roles = null;
        CredencialDTO credencialDTO = userProviderService.getCredentialByLogin(resolve.getLogin());

        // TODO verificar se vai fucnionar com o login
        roles = Objects.nonNull(credencialDTO) ? credencialDTO.getRoles() : Arrays.asList();

        credencialDTO.setId(resolve.getUserId());
        credencialDTO.setLogin(resolve.getLogin());
        credencialDTO.setEmail(resolve.getEmail());
        credencialDTO.setName(resolve.getName());
        credencialDTO.setRoles(roles);
        credencialDTO.setPassword(null);
        return credencialDTO;
    }

    /**
     * Realiza a inclusão ou alteração de senha.
     *
     * @param authUserDTO -
     * @param token           -
     * @return -
     */
    public CredencialDTO resetPassword(final AuthUserDTO authUserDTO, final String token) {
        AuthClaimResolve resolve = getClaimResolve(token);

        authUserDTO.setUserId(resolve.getUserId());
        authUserDTO.setType(resolve.getResetPasswordType());
        CredencialDTO usuario = userProviderService.resetPassword(authUserDTO);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setLogin(usuario.getLogin());
        authDTO.setPassword(authUserDTO.getNewPassword());
        return loginAccess(authDTO);
    }

    /**
     * Valida o token de alteração de senha.
     *
     * @param token -
     */
    public boolean getInfoByTokenValidacao(final String token) {
        AuthClaimResolve resolve = getClaimResolve(token);

        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setType(resolve.getResetPasswordType());

        Long userId = resolve.getUserId();
        CredencialDTO credential = userProviderService.getCredentialByLogin(resolve.getLogin());//usuarioService.getById(userId);
        return authUserDTO.isRecover() || (authUserDTO.isActivate() && !credential.isActiveState());
    }


    /**
     * Verifica se os campos de preechimento obrigatório foram informados.
     *
     * @param authDTO -
     */
    private void validateMandatoryFieldsOnLoginAccess(final AuthDTO authDTO) {
        if (Strings.isEmpty(authDTO.getLogin()) || Strings.isEmpty(authDTO.getPassword())) {
            throw new BusinessException(ApiMessageCode.ERROR_MANDATORY_FIELDS);
        }
    }

    /**
     * Verifica se o {@link CredencialDTO} informado é valido no momento do login.
     *
     * @param usuario -
     */
    private void validateLoginUser(CredencialDTO usuario) {
        if (usuario == null) {
            throw new BusinessException(ApiMessageCode.ERROR_USER_PASSWORD_NOT_MATCH);
        }

        registerCredentialInSecurityContext(usuario);

        if (!usuario.isActiveState()) {
            throw new BusinessException(ApiMessageCode.ERROR_INACTIVE_USER);
        }
    }

    /**
     * Retorna a instância de {@link AuthClaimResolve}.
     *
     * @param token -
     * @return -
     */
    private AuthClaimResolve getClaimResolve(final String token) {
        String value = getAccessToken(token);
        TokenBuilder builder = new TokenBuilder(keyToken);
        Map<String, Claim> claims = builder.getClaims(value);

        if (claims == null) {
            throw new BusinessException(ApiMessageCode.ERROR_INVALID_TOKEN);
        }
        return AuthClaimResolve.newInstance(claims);
    }

    /**
     * Retorna o token de acesso recuperados da instância
     * {@link HttpServletRequest}.
     *
     * @return -
     */
    private String getAccessToken(final String value) {
        String accessToken = null;

        if (!Strings.isEmpty(value)) {
            accessToken = value.replaceAll(Constante.HEADER_AUTHORIZATION_BEARER, "").trim();
        }
        return accessToken;
    }
}
