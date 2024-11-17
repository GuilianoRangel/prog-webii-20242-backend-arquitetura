package br.ueg.progweb2.arquitetura.service.impl;

import br.ueg.progweb2.arquitetura.exceptions.ApiMessageCode;
import br.ueg.progweb2.arquitetura.exceptions.BusinessException;
import br.ueg.progweb2.arquitetura.exceptions.DevelopmentException;
import br.ueg.progweb2.arquitetura.exceptions.FieldResponse;
import br.ueg.progweb2.arquitetura.interfaces.IConverter;
import br.ueg.progweb2.arquitetura.mapper.GenericUpdateMapper;
import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.model.dtos.SearchField;
import br.ueg.progweb2.arquitetura.model.dtos.SearchFieldValue;
import br.ueg.progweb2.arquitetura.reflection.ReflectionUtils;
import br.ueg.progweb2.arquitetura.reflection.SearchReflection;
import br.ueg.progweb2.arquitetura.repository.model.ISearchTypePredicate;
import br.ueg.progweb2.arquitetura.service.CrudService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public abstract class GenericCrudService<
            MODEL extends GenericModel<TYPE_PK>,
            TYPE_PK,
            REPOSITORY extends JpaRepository<MODEL, TYPE_PK>
        > implements CrudService <
            MODEL,
            TYPE_PK
        >{

    private static final Logger log = LoggerFactory.getLogger(GenericCrudService.class);

    @Autowired
    private ApplicationContext context;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private GenericUpdateMapper<MODEL, TYPE_PK> mapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected REPOSITORY repository;

    private Class<TYPE_PK> entityClass;

    public List<MODEL> listAll(){
        return repository.findAll();
    }

    public Page<MODEL> listAllPage(Pageable page) {
        return repository.findAll(page);
    }

    @Autowired
    private MessageSource messageSource;

    @Override
    public MODEL create(MODEL dado) {
        prepareToCreate(dado);
        setListReferences(dado);
        validateMandatoryFields(dado);
        validateBusinessLogic(dado);
        validateBusinessLogicForInsert(dado);
        MODEL saved = repository.saveAndFlush(dado);
        //TODO verificar para buscar os dados no banco novmente para atualizar dados relacionados
        return this.getById(saved.getId());
    }

    protected abstract void prepareToCreate(MODEL dado);

    @Override
    public MODEL update(MODEL dataToUpdate){
        var dataDB = validateIdModelExists(dataToUpdate.getId());
        setListReferences(dataToUpdate);
        validateMandatoryFields(dataToUpdate);
        validateBusinessLogic(dataToUpdate);
        validateBusinessLogicForUpdate(dataToUpdate);
        updateDataDBFromUpdate(dataToUpdate, dataDB);
        return repository.save(dataDB);
    }

    protected void updateDataDBFromUpdate(MODEL dataToUpdate, MODEL dataDB){
        mapper.updateModelFromModel(dataDB, dataToUpdate);
    };

    @Override
    public MODEL getById(TYPE_PK id){
        return this.validateIdModelExists(id);
    }

    @Override
    public MODEL deleteById(TYPE_PK id){
        MODEL modelToRemove = this.validateIdModelExists(id);
        this.repository.delete(modelToRemove);
        return modelToRemove;
    }

    protected MODEL validateIdModelExists(TYPE_PK id){
        boolean valid = true;
        MODEL dadoBD = null;

        if(Objects.nonNull(id)) {
            dadoBD = this.internalGetById(id);
            if (dadoBD == null) {
                valid = false;
            }
        }else{
            valid = false;
        }

        if(Boolean.FALSE.equals(valid)){
            throw new BusinessException(ApiMessageCode.ERROR_RECORD_NOT_FOUND);
        }
        return dadoBD;
    }

    private MODEL internalGetById(TYPE_PK id){
        Optional<MODEL> byId = repository.findById(id);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }

    protected abstract void validateBusinessLogicForInsert(MODEL dado);

    protected abstract void validateBusinessLogicForUpdate(MODEL dado) ;

    protected abstract void validateBusinessLogic(MODEL dado) ;

    protected void validateMandatoryFields(MODEL dado) {
        List<String> mandatoryFieldsNotFilled = ReflectionUtils.getMandatoryFieldsNotFilled(dado);
        if (!mandatoryFieldsNotFilled.isEmpty()) {
            List<FieldResponse> fieldResponseErros = mandatoryFieldsNotFilled.stream().map(
                    s -> {
                        String messageI18n = messageSource.getMessage(
                                ApiMessageCode.ARQ_MANDATORY_FIELD.toString(),
                                s.lines().toArray(),
                                LocaleContextHolder.getLocale()
                        );
                        return new FieldResponse(s, messageI18n);
                    }).toList();
            throw new BusinessException(ApiMessageCode.ERROR_MANDATORY_FIELDS, fieldResponseErros);
        }
    }

    public Class<TYPE_PK> getEntityType() {
        //TODO MELHORIA - verificar a posição do modelo dinamicamente verificando o tipo entidade
        if(Objects.isNull(this.entityClass)){
            this.entityClass = (Class<TYPE_PK>) ((ParameterizedType) this.getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return this.entityClass;
    }



    private void setListReferences(MODEL modelo) {
        for (Field entidadeField : ReflectionUtils.getEntityFields(modelo)) {
            if(
                    Collection.class.isAssignableFrom(entidadeField.getType())
            ){
                ParameterizedType listType = (ParameterizedType) entidadeField.getGenericType();
                Class<?> listClass = (Class<?>) listType.getActualTypeArguments()[0];
                if(GenericModel.class.isAssignableFrom(listClass)) {
                    var list = (Collection<GenericModel<?>>) ReflectionUtils.getFieldValue(modelo, entidadeField.getName());
                    if(Objects.isNull(list)) { continue; }
                    System.out.println(listClass.getSimpleName());
                    for (GenericModel<?> iEntidade : list) {
                        var entidadeFields = ReflectionUtils.getEntityFields(iEntidade);
                        for (Field fieldAux : entidadeFields) {
                            if(fieldAux.getType().isAssignableFrom(modelo.getClass())){
                                ReflectionUtils.setFieldValue(iEntidade, fieldAux.getName(), modelo);
                            }
                        }

                    }
                }
            }
        }

    }



    @Override
    public List<SearchField> listSearchFields() {
        return SearchReflection.getSearchFieldList(this.context, this.getEntityType());
    }

    public class SearchEntity implements Specification<MODEL> {
        private final List<SearchFieldValue> searchFieldValues;
        private final Class<?> entityClass;

        public SearchEntity(Class<?> entityClass, List<SearchFieldValue> searchFieldValues) {
            this.searchFieldValues = searchFieldValues;
            this.entityClass = entityClass;
        }

        @Override
        public Predicate toPredicate(Root<MODEL> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            //TODO tratar como objeto e não como strinig;
            //String strToSearch = searchFieldValue.getValue().toString().toLowerCase();
            List<Predicate> listPredicate = new ArrayList<>();
            for (SearchFieldValue fieldValue : searchFieldValues) {
                listPredicate.add(getPredicate(root, cb, fieldValue));
            }
            Predicate firstPredicate = listPredicate.get(0);

            for (int i = 1; i < listPredicate.size(); i++) {
                firstPredicate = cb.and(firstPredicate, listPredicate.get(i));
            }


            return firstPredicate;
        }

        private Predicate getPredicate(Root<MODEL> root, CriteriaBuilder cb, SearchFieldValue valueSearch) {
            Object value = getValue(valueSearch);
            valueSearch.setObjectValue(value);

            validSearchFieldName(valueSearch);
            ISearchTypePredicate searchTypePredicate =  valueSearch.getSearchType().getPredicateExecute();

            if(Objects.nonNull(searchTypePredicate)){
                return searchTypePredicate.execute(root, cb, valueSearch);
            }else{
                throw new DevelopmentException("tipo Busca:" + valueSearch.getSearchType() + " não implementado !");
            }
            /*//TODO Verificar busca case insensitive
            switch (valueSearch.getSearchType()) {
                case EQUAL -> {
                    return cb.equal(root.get(valueSearch.getName()), valueSearch.getObjectValue());
                    //return cb.equal(departmentJoin(root).<String>get(searchCriteria.getFilterKey()), searchCriteria.getValue());
                }
                case BEGINS_WITH -> {
                    return cb.like(cb.lower(root.get(valueSearch.getName())), valueSearch.getObjectValue().toString().toLowerCase() + "%");
                }
                default -> {
                    throw new DevelopmentException("tipo Busca:" + valueSearch.getSearchType() + " não existe !");
                }
            }*/
        }

        private Field validSearchFieldName(SearchFieldValue valueSearch) {
            Field entidadeField;
            String fieldName = valueSearch.getName();
            if(fieldName.contains(".")){
                fieldName = fieldName.split("\\.")[0];
            }
            try {
                entidadeField = ReflectionUtils.getEntityField(entityClass, fieldName);
            } catch (NoSuchFieldException e) {
                throw new DevelopmentException("Campo informado para busca:" + fieldName + " não existe na entidade: " + this.entityClass.getName());
            }
            return entidadeField;
        }
    }

    public List<MODEL> searchFieldValues(List<SearchFieldValue> searchFieldValues){
        try{
            Class<?> entityClass = this.getEntityType();


            JpaRepository entityRepository = ReflectionUtils.getEntityRepository(this.context, this.getEntityType());
            if(entityRepository instanceof JpaSpecificationExecutor){
                JpaSpecificationExecutor<MODEL> jpaSpecificationExecutor = (JpaSpecificationExecutor<MODEL>) entityRepository;
                List<MODEL> all = jpaSpecificationExecutor.findAll(new SearchEntity(entityClass, searchFieldValues));
                return all;
            }else{
                throw new DevelopmentException("Repository not implement JpaSpecificationExecutor:"+ entityRepository.getClass().getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public Page<MODEL> searchFieldValuesPage(Pageable page, List<SearchFieldValue> searchFieldValues){
        try{
            Class<?> entityClass = this.getEntityType();

            JpaRepository entityRepository = ReflectionUtils.getEntityRepository(this.context, this.getEntityType());
            if(entityRepository instanceof  JpaSpecificationExecutor){
                JpaSpecificationExecutor<MODEL> jpaSpecificationExecutor = (JpaSpecificationExecutor<MODEL>) entityRepository;
                Page<MODEL> all = jpaSpecificationExecutor.findAll(new SearchEntity(entityClass, searchFieldValues),page);
                return all;
            }else{
                throw new DevelopmentException("Repository not implement JpaSpecificationExecutor:"+ entityRepository.getClass().getName());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        //TODO ver como retornar um pageable vazio
        return null;
    }
    private Object getValue(SearchFieldValue valueSearch) {
        Object value;
        String converterClass = valueSearch.getType().concat("Converter");
        converterClass = converterClass.substring(0,1).toLowerCase().concat(converterClass.substring(1));
        try {
            IConverter converter = (IConverter) this.context.getBean(converterClass);
            value = converter.converter(valueSearch.getValue());
        }catch (Exception e){
            log.info("Erro ao Converter, ou Converter Não encontrado: "+converterClass);
            value = valueSearch.getValue();
        }
        return value;
    }
}
