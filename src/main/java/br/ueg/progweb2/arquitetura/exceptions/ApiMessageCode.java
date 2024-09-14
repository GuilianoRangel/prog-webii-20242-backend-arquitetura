package br.ueg.progweb2.arquitetura.exceptions;

import lombok.Getter;

@Getter
public enum ApiMessageCode implements MessageCode {


    ERROR_UNEXPECTED("ME001", 500),
    ERROR_RECORD_NOT_FOUND("ME002", 404),
    ERROR_BD("ME003", 400),

    ERROR_MANDATORY_FIELDS("ME005", 400),

    MSG_OPERATION_SUCESS("MSG-000", 200)
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
