package br.ueg.progweb2.exampleuse.configuration;


import br.ueg.progweb2.arquitetura.config.ApiSecurityConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig extends ApiSecurityConfig {
    @Value("${app.api.base}")
    private String apiBase;

    @Override
    protected void configureHttpSecurity(HttpSecurity http)  throws Exception{
    }

    @Override
    protected List<String> getCustomFreeAccessPaterns() {
        return Arrays.asList(apiBase.concat("/domain-model/list-actives"));
    }
}
