package br.ueg.progweb2.arquitetura.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static br.ueg.progweb2.arquitetura.config.Constante.HEADER_AUTHORIZATION;
import static br.ueg.progweb2.arquitetura.config.Constante.HEADER_AUTHORIZATION_BEARER;

@Service
@NoArgsConstructor
public class LogoutService implements LogoutHandler {

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader(HEADER_AUTHORIZATION);
    if (authHeader == null ||!authHeader.startsWith(HEADER_AUTHORIZATION_BEARER)) {
      return;
    }
    SecurityContextHolder.clearContext();
  }
}
