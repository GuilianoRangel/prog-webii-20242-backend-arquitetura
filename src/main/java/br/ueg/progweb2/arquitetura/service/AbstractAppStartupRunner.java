package br.ueg.progweb2.arquitetura.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


public abstract class AbstractAppStartupRunner implements ApplicationRunner {
    public static final String CREATE_DROP="create-drop";

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractAppStartupRunner.class);

    public void initDados(){
        LOG.info("Inicio da execução do InitDados!");
        if(!this.ddlAuto.equalsIgnoreCase(CREATE_DROP)){
            return;
        }
        this.runInitData();
    }

    public abstract void runInitData();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            this.initDados();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
