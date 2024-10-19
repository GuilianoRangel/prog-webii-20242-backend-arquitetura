package br.ueg.progweb2.arquitetura.reflection;

import br.ueg.progweb2.arquitetura.controllers.GenericCRUDController;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestControllerInspector {

    private final ApplicationContext applicationContext;

    public RestControllerInspector(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Lista todas as classes anotadas com @RestController
     * @return Lista de nomes de classes anotadas com @RestController
     */
    public List<Class<?>> listRestControllers() {
        // Obter todos os beans gerenciados pelo Spring
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        // Filtrar as classes anotadas com @RestController
        return Arrays.stream(beanNames)
                .map(applicationContext::getType) // Obter o tipo do bean
                .filter(beanClass -> beanClass != null &&
                        AnnotationUtils.findAnnotation(beanClass, RestController.class) != null) // Verificar a anotação
                .collect(Collectors.toList()); // Coletar as classes anotadas
    }

    /**
     * Obtém um bean pelo nome da classe (com a primeira letra minúscula)
     * @param className Nome da classe (primeira letra minúscula)
     * @return Bean do Spring
     */
    public GenericCRUDController<?,?,?,?,?,?,?,?> getRestControllerBeanByClassName(String className) {
        String classNameAux = className.substring(0,1).toLowerCase().concat(className.substring(1));
        return (GenericCRUDController<?,?,?,?,?,?,?,?>) applicationContext.getBean(classNameAux);
    }
}

