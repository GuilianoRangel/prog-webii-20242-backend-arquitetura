package br.ueg.progweb2.exampleuse.controller;

import br.ueg.progweb2.arquitetura.controllers.GenericCRUDController;
import br.ueg.progweb2.arquitetura.exceptions.MessageResponse;
import br.ueg.progweb2.exampleuse.mapper.DomainModelMapper;
import br.ueg.progweb2.exampleuse.model.DomainModel;
import br.ueg.progweb2.exampleuse.model.dtos.DomainModelDTO;
import br.ueg.progweb2.exampleuse.service.DomainModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RequestMapping(path = "${app.api.base}/domain-model")
@RestController
public class DomainModelController  extends GenericCRUDController<
        DomainModelDTO,
        DomainModelDTO,
        DomainModelDTO,
        DomainModelDTO,
        DomainModel,
        Long,
        DomainModelService,
        DomainModelMapper
        > {

        @GetMapping(path = "list-actives",
                produces = {MediaType.APPLICATION_JSON_VALUE})
        @Operation(description = "lista todos domain model ativos", responses = {
                @ApiResponse(responseCode = "200", description = "Listagem de domain model ativos",
                        useReturnTypeSchema = true),
                @ApiResponse(responseCode = "404", description = "Registro não encontrado",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = MessageResponse.class))),
                @ApiResponse(responseCode = "403", description = "Acesso negado",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = MessageResponse.class))),
                @ApiResponse(responseCode = "400", description = "Erro de Negócio",
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                schema = @Schema(implementation = MessageResponse.class)))
        })
        public ResponseEntity<List<DomainModelDTO>> listAllActives() {
            List<DomainModelDTO> modelList = mapper.fromModelToDTOList(service.listAllActives());
            return ResponseEntity.of(
                    Optional.ofNullable(modelList)
            );
        }
    @PostMapping(path = "remove-inactives",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Remove todos os domain model inativos", responses = {
            @ApiResponse(responseCode = "200", description = "Quantos registros foram removidos",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de Negócio",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    @PreAuthorize("hasRole('ROLE_DOMAINMODEL_REMOVEALL')")
    public ResponseEntity<Long> removeAllInactives() {

        return ResponseEntity.of(
                Optional.ofNullable(service.removeAllInatives())
        );
    }
}

