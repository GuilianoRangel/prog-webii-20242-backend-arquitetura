package br.ueg.progweb2.arquitetura.service.impl;

import br.ueg.progweb2.arquitetura.exceptions.ApiMessageCode;
import br.ueg.progweb2.arquitetura.exceptions.BusinessException;
import br.ueg.progweb2.arquitetura.exceptions.FieldResponse;
import br.ueg.progweb2.arquitetura.mapper.GenericUpdateMapper;
import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.reflection.ReflectionUtils;
import br.ueg.progweb2.arquitetura.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class GenericCrudService<
            MODEL extends GenericModel<TYPE_PK>,
            TYPE_PK,
            REPOSITORY extends JpaRepository<MODEL, TYPE_PK>
        > implements CrudService <
            MODEL,
            TYPE_PK
        >{
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
}
