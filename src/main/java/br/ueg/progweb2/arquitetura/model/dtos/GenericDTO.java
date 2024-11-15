package br.ueg.progweb2.arquitetura.model.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class GenericDTO <TYPE_PK> {
    @Schema(description = "Método abstrato para retornar o ID do DTO", example = "1", type = "integer", format = "int64")
    public abstract TYPE_PK getId();
    public abstract void setId(TYPE_PK id);
}
