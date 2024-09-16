package br.ueg.progweb2.exampleuse.service.impl;

import br.ueg.progweb2.arquitetura.service.AbstractAppStartupRunner;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.repository.DomainModelRespository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AppStartupRunner extends AbstractAppStartupRunner {

    private static final Logger LOG =
            LoggerFactory.getLogger(AppStartupRunner.class);

    @Autowired
    private DomainModelRespository domainModelRespository;

    public void runInitData(){
        var faker = new Faker(Locale.ENGLISH);

        DomainModel domainModel = null;
        for (int i = 1; i<=30; i++){
            domainModel = DomainModel.builder()
                    .id((long) i)
                    .description(faker.backToTheFuture().character())
                    .active(i%10!=0)
                    .build();
            this.domainModelRespository.save(domainModel);
        }

        LOG.info("Fim da execução");
    }

}
