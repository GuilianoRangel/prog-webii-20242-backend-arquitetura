package br.ueg.progweb2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({
        @PropertySource(value = "classpath:application-test.properties")
})
@SpringBootApplication()
public class ArqApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArqApplication.class, args);
    }
}
