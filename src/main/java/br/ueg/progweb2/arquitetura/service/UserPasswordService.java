package br.ueg.progweb2.arquitetura.service;

import br.ueg.progweb2.arquitetura.model.dtos.AuthDTO;
import br.ueg.progweb2.arquitetura.model.dtos.CredencialDTO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordService {
    public static Boolean loginByPassword(CredencialDTO userCredential, AuthDTO authDTO) {
        if (!authDTO.getLogin().equals(userCredential.getLogin())) {
            return false;
        }
        // Tratamento de senha sem enconde (senha com menos de 21 caracteres
        if (userCredential.getPassword().length() <= 20) {
            if (authDTO.getPassword().equals(userCredential.getPassword())) {
                return true;
            }
        } else {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if (bCryptPasswordEncoder.matches(authDTO.getPassword(), userCredential.getPassword())) {
                return true;
            }
        }
        return false;
    }
}
