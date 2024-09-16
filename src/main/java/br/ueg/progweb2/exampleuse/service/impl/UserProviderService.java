package br.ueg.progweb2.exampleuse.service.impl;

import br.ueg.progweb2.arquitetura.model.dtos.CredencialDTO;
import br.ueg.progweb2.arquitetura.model.dtos.AuthUserDTO;
import br.ueg.progweb2.arquitetura.service.IUserProviderService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserProviderService implements IUserProviderService {



    @Override
    public CredencialDTO getCredentialByLogin(String username) {

        return getCredencialDTO();
    }

    private static CredencialDTO getCredencialDTO() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode("admin");
        return CredencialDTO.builder()
                .login("admin")
                .id(1L)
                .name("Admin")
                .email("admin@admin.com.br")
                .roles(Arrays.asList(
                        "ROLE_DOMAINMODEL_REMOVEALL",
                        "ROLE_DOMAINMODEL_CREATE",
                        "ROLE_DOMAINMODEL_READ",
                        "ROLE_DOMAINMODEL_UPDATE",
                        "ROLE_DOMAINMODEL_DELETE",
                        "ROLE_DOMAINMODEL_READ_ALL"
                ))
                .activeState(true)
                .password(encodedPassword)
                .build();
    }

    @Override
    public CredencialDTO resetPassword(AuthUserDTO authUserDTO) {
        return null;
        //return usuarioMapper.toCredentialDTO(this.usuarioService.redefinirSenha(usuarioSenhaDTO));
    }

    @Override
    public CredencialDTO getCredentialByEmail(String email) {
        //TODO retornando fixo
        return getCredencialDTO();
    }
}
