package br.ueg.progweb2.arquitetura.exceptions;

import lombok.Getter;

@Getter
public enum ApiMessageCode implements MessageCode {


    ERROR_UNEXPECTED("ME001", 500),
    ERROR_RECORD_NOT_FOUND("ME002", 404),
    ERROR_BD("ME003", 400),
    ERROR_MANDATORY_FIELDS("ME004", 400),

    ERROR_INVALID_TOKEN("ME005", 403),
    ERROR_USER_NOT_FOUND("ME006", 404),
    ERROR_USER_PASSWORD_NOT_MATCH("ME007", 400),
    ERROR_INACTIVE_USER("ME008",400),

    MSG_OPERATION_SUCESS("MSG-000", 200),

    ARQ_MANDATORY_FIELD("MEA01", 0),

    ERROR_SEARCH_PARAMETERS_NOT_DEFINED("ME009", 400),

    SEARCH_FIELDS_RESULT_NONE("ME010", 404)
    ;
    private final String code;

    private final Integer status;

    /**
     * Construtor da classe.
     *
     * @param code -
     * @param status -
     */
    ApiMessageCode(final String code, final Integer status) {
        this.code = code;
        this.status = status;
    }

    /**
     * @see Enum#toString()
     */
    @Override
    public String toString() {
        return code;
    }
}
