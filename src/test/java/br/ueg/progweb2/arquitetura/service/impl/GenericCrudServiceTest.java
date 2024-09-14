package br.ueg.progweb2.arquitetura.service.impl;

import br.ueg.progweb2.exampleuse.ArqApplication;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.repository.DomainModelRespository;
import br.ueg.progweb2.exampleuse.service.impl.DomainModelServiceImpl;
import com.github.javafaker.Faker;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Documentação base utilizada:
 * https://howtodoinjava.com/spring-boot/spring-boot-test-controller-service-dao/
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ArqApplication.class)
public class GenericCrudServiceTest {

    private static Faker faker;

    @MockBean
    private DomainModelRespository repository;

    @InjectMocks
    private DomainModelServiceImpl genericCrudService;

    @BeforeAll
    static void initializeFaker() {
        faker = new Faker(Locale.ENGLISH);
    }
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAllShouldReturnDomainModels(){
        List<DomainModel> domainModels = this.populateDomainModelList();

        when(this.repository.findAll()).thenReturn(domainModels);

        List<DomainModel> domainModelResult = genericCrudService.listAll();

        //Verifica se o objeto do assertThat é igual ao objeto informado no isEqualTo
        Assertions.assertThat(domainModelResult).isEqualTo(domainModels);

        // Verifica se o primeiro valor (expected) é igual ao segundo valor (actual)
        assertEquals(10, domainModelResult.size());

        // Verifica se o método findAll do objeto repository foi invocado 1 vez
        verify(repository, times(1)).findAll();
    }

    @Test
    void createShouldReturnNewModel(){
        // modelo retornado pelo save do repository (com id e descrição toUpperCase)
        DomainModel domainModelSaved = getDomainModel();

        // modelo que será enviado para o serviço salvar (sem id e descricao normal)
        DomainModel domainModelToCreate = cloneDomainModel(domainModelSaved);
        domainModelToCreate.setId(null);

        domainModelSaved.setDescription(domainModelSaved.getDescription().toUpperCase());

        // modelo que será enviado para o repository salvar(sem id e com
        DomainModel domainModelToSave = cloneDomainModel(domainModelSaved);
        domainModelToSave.setId(null);

        when(this.repository.saveAndFlush(eq(domainModelToSave))).thenReturn(domainModelSaved);
        //when(this.repository.saveAndFlush(any())).thenReturn(domainModelSaved);
        when(this.repository.findById(domainModelSaved.getId())).thenReturn(Optional.of(domainModelSaved));

        DomainModel domainModelResult = genericCrudService.create(domainModelToCreate);

        Assertions.assertThat(domainModelResult).isNotNull();

        assertEquals(domainModelSaved, domainModelResult);
        assertEquals(domainModelSaved.getDescription(), domainModelResult.getDescription());

    }

    private List<DomainModel> populateDomainModelList() {
        List<DomainModel> domainModels = new ArrayList<DomainModel>();
        for(int i = 0; i < 10; i++) {
            DomainModel domainModel = new DomainModel();
            domainModel.setId(Long.valueOf(i));
            domainModel.setDescription(faker.cat().name());
            domainModels.add(domainModel);
        }
        return domainModels;
    }

    private DomainModel getDomainModel() {
        DomainModel domainModel = new DomainModel();
        domainModel.setId(Long.valueOf(1));
        domainModel.setDescription(faker.cat().name());
        return domainModel;
    }

    private DomainModel cloneDomainModel(DomainModel dado) {
        DomainModel domainModel = new DomainModel();
        domainModel.setId(dado.getId());
        domainModel.setDescription(dado.getDescription());
        return domainModel;
    }
}
