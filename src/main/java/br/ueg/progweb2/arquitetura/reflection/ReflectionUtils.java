package br.ueg.progweb2.arquitetura.reflection;

import br.ueg.progweb2.arquitetura.controllers.AbstractController;
import br.ueg.progweb2.arquitetura.controllers.GenericCRUDController;
import br.ueg.progweb2.arquitetura.controllers.enums.ISecurityRole;
import br.ueg.progweb2.arquitetura.model.GenericModel;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReflectionUtils {

    /**
     * Método para retornar a lista de atributos de uma classe filha de GenericModel
     * com os metadados dos atributos do modelo
     * @param model - modelo que espero obter a lista de fields
     * @return Lista de fields ou uma lista vazia se não tiver fields;
     */
    public static List<Field> getEntityFields(GenericModel<?> model){
        List<Field> resultFields = new ArrayList<Field>();
        Class<?> clazz = model.getClass();
        //TODO ver questão para parar antes de Object
        while(clazz != null && !clazz.equals(GenericModel.class)){
            Field[] modelFields = clazz.getDeclaredFields();
            for(Field field : modelFields){
                boolean isModelField = field.isAnnotationPresent(Column.class)
                        || field.isAnnotationPresent(JoinColumn.class)
                        || field.isAnnotationPresent(OneToMany.class);
                if(isModelField){
                    resultFields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return resultFields;
    }

    public static List<Field> getFields(Object object){
        List<Field> resultFields = new ArrayList<Field>();
        Class<?> clazz = object.getClass();
        while(clazz != null && !clazz.equals(Object.class)){
            Field[] modelFields = clazz.getDeclaredFields();
            resultFields.addAll(Arrays.asList(modelFields));
            clazz = clazz.getSuperclass();
        }
        return resultFields;
    }

    /**
     * Método para retornar a lista de atributos obrigatórios de uma classe filha de GenericModel
     * com os metadados deste atributos
     * @param model - modelo que espero obter a lista de fields obrigatórios
     * @return Lista de fields obrigatórios ou uma lista vazia se não tiver atributos obrigatórios;
     */
    public static List<Field> getMandatoryFields(GenericModel<?> model){
        List<Field> modelFields = ReflectionUtils.getEntityFields(model);
        List<Field> mandatoryFields = new ArrayList<>();

        for(Field field : modelFields){
            boolean isColumnField = field.isAnnotationPresent(Column.class);
            if(isColumnField){
                Column column = field.getAnnotation(Column.class);
                boolean isGeneratedValue = field.isAnnotationPresent(GeneratedValue.class);
                if(Boolean.FALSE.equals(column.nullable()) && !isGeneratedValue) {
                    mandatoryFields.add(field);
                }
            }else if ( field.isAnnotationPresent(JoinColumn.class)){
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                if(Boolean.FALSE.equals(joinColumn.nullable())){
                    mandatoryFields.add(field);
                }
            }
        }
        return mandatoryFields;
    }


    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(GenericModel<?> model, Field field, T fieldType){
        String fieldGetMethodName = "get" + StringUtils.uCFirst(field.getName());
        
        Class<? extends GenericModel> modelClass = model.getClass();
        try {
            Method fieldGetMethod = modelClass.getMethod(fieldGetMethodName);
            return (T) fieldGetMethod.invoke(model);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Error getting field value from "+model.getClass().getName()+
                            "."+field.getName(),e
                    );
        }
    }

    public static List<String> getMandatoryFieldsNotFilled(GenericModel<?> model) {
        List<Field> mandatoryFields = ReflectionUtils.getMandatoryFields(model);
        List<String> mandatoryFieldNames = new ArrayList<>();
        for (Field field : mandatoryFields) {
            Object fieldValue = getFieldValue(model, field, field.getType());
            if (Objects.isNull(fieldValue)) {
                mandatoryFieldNames.add(field.getName());
            } else if (String.class.isAssignableFrom(field.getType())) {
                String fieldValueString = (String) fieldValue;
                if (fieldValueString.isEmpty()) {
                    mandatoryFieldNames.add(field.getName());
                }
            } else if(GenericModel.class.isAssignableFrom(field.getType())){
                GenericModel genericModel = (GenericModel) fieldValue;
                if(Objects.isNull(genericModel.getId())){
                    mandatoryFieldNames.add(field.getName());
                }
            }
        }
        return mandatoryFieldNames;
    }

    public static Field getEntityField(GenericModel<?> entidade, String fieldName) throws NoSuchFieldException {
        Field fieldResult = null;
        for (Field entidadeField : getEntityFields(entidade)) {
            if(entidadeField.getName().equals(fieldName)){
                fieldResult = entidadeField;
                break;
            }
        }
        if (fieldResult == null) {
            throw new NoSuchFieldException(fieldName);
        }
        return fieldResult;
    }

    public static Field getField(Object object, String fieldName) throws NoSuchFieldException {
        Field fieldResult = null;
        for (Field entidadeField : getFields(object)) {
            if(entidadeField.getName().equals(fieldName)){
                fieldResult = entidadeField;
                break;
            }
        }
        if (fieldResult == null) {
            throw new NoSuchFieldException(fieldName);
        }
        return fieldResult;
    }

    public static Object getFieldValue(Object entidade, String fieldName) {
        try {
            Class<?> entidadeClass = entidade.getClass();
            Field field = getField(entidade, fieldName);
            String methodGetFieldName = "get"+StringUtils.uCFirst(field.getName());
            return entidadeClass.getMethod(methodGetFieldName).invoke(entidade);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setFieldValue(GenericModel<?> entidade, String fieldName, Object value)  {
        try {
            Class<? extends GenericModel> entidadeClass = entidade.getClass();
            Field field = getEntityField(entidade, fieldName);
            String methodGetFieldName = "set"+StringUtils.uCFirst(field.getName());
            entidadeClass.getMethod(methodGetFieldName, field.getType()).invoke(entidade, value);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Método para retornar a lista do valor das constantes que inicial com ROLE_
     * buscando até a classe GenericCRUDController
     * @param controller - Controller para buscar as ROLE para criar banco de autorização
     * @return Lista de String das roles do controlador;
     */
    public static List<ISecurityRole> getRoleConstantFromController(GenericCRUDController<?,?,?,?,?,?,?,?> controller){
        List<ISecurityRole> resultFields = new ArrayList<>();
        Class<?> clazz = controller.getClass();
        while(clazz != null && !clazz.equals(AbstractController.class)){
            Field[] modelFields = clazz.getDeclaredFields();
            for(Field field : modelFields){
                boolean isRoleConstant = field.getType().isAssignableFrom(ISecurityRole.class);
                if(isRoleConstant){
                    try {
                        resultFields.add((ISecurityRole) field.get(controller));
                    } catch (IllegalAccessException e) {
                        //TODO fazer log
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
        return resultFields;
    }

}
