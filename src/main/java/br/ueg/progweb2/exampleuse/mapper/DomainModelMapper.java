package br.ueg.progweb2.exampleuse.mapper;

import br.ueg.progweb2.arquitetura.mapper.SimpleGenericMapper;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.model.dtos.DomainModelDTO;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring"
)
public interface DomainModelMapper
        extends SimpleGenericMapper<
        DomainModelDTO,
        DomainModel,
        Long
        > {
}
