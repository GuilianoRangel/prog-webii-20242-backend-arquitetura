package br.ueg.progweb2.arquitetura.controllers;

import br.ueg.progweb2.arquitetura.controllers.enums.CrudSecurityRole;
import br.ueg.progweb2.arquitetura.controllers.enums.ISecurityRole;
import br.ueg.progweb2.arquitetura.exceptions.ApiMessageCode;
import br.ueg.progweb2.arquitetura.exceptions.BusinessException;
import br.ueg.progweb2.arquitetura.exceptions.MessageResponse;
import br.ueg.progweb2.arquitetura.mapper.GenericMapper;
import br.ueg.progweb2.arquitetura.model.GenericModel;
import br.ueg.progweb2.arquitetura.model.dtos.SearchField;
import br.ueg.progweb2.arquitetura.model.dtos.SearchFieldValue;
import br.ueg.progweb2.arquitetura.reflection.ReflectionUtils;
import br.ueg.progweb2.arquitetura.service.CrudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class GenericCRUDController<
        DTO,
        DTOCreate,
        DTOUpdate,
        DTOList,
        MODEL extends GenericModel<TYPE_PK>,
        TYPE_PK,
        SERVICE extends CrudService<
                MODEL,
                TYPE_PK>,
        MAPPER extends GenericMapper<DTO,DTOCreate, DTOUpdate, DTOList , MODEL, TYPE_PK>
        > extends AbstractController implements SecuritedController {
    public static final ISecurityRole ROLE_CREATE   = CrudSecurityRole.CREATE;
    public static final ISecurityRole ROLE_READ     = CrudSecurityRole.READ;
    public static final ISecurityRole ROLE_UPDATE   = CrudSecurityRole.UPDATE;
    public static final ISecurityRole ROLE_DELETE   = CrudSecurityRole.DELETE;
    public static final ISecurityRole ROLE_READ_ALL = CrudSecurityRole.READ_ALL;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected SERVICE service;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected MAPPER mapper;

    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_CREATE))")
    @PostMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(description = "Método utilizado para realizar a inclusão de um entidade",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Entidade Incluida",
                            useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "403", description = "Acesso negado",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Erro de Negócio",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageResponse.class)))
            })
    public ResponseEntity<DTO> create(@RequestBody DTOCreate dto) {
        MODEL inputModel = mapper.fromModelCreatedToModel(dto);
        DTO resultDTO = mapper.toDTO(service.create(inputModel));
        return ResponseEntity.ok(resultDTO);
    }


    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_UPDATE))")
    @PutMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Método utilizado para altlerar os dados de uma entidiade", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem geral",
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
    }
    )
    public ResponseEntity<DTO> update(
            @RequestBody DTOUpdate dto,
            @PathVariable("id") TYPE_PK id) {
        MODEL inputModel = mapper.fromModelUpdatedToModel(dto);
        inputModel.setId(id);
        MODEL modelSaved = service.update(inputModel);
        return ResponseEntity.ok(mapper.toDTO(modelSaved));
    }

    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_READ_ALL))")
    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "lista todos modelos", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem geral",
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
    public ResponseEntity<List<DTOList>> listAll() {
        List<DTOList> modelList = mapper.fromModelToDTOList(service.listAll());
        return ResponseEntity.of(
                Optional.ofNullable(modelList)
        );
    }


    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_READ_ALL))")
    @GetMapping(
            path = "/page",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "lista todos modelos paginada", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem geral paginada",
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
    public ResponseEntity<Page<DTOList>> listAllPage(@PageableDefault(page = 0, size = 5) Pageable page){
        Page<MODEL> pageEntidade = service.listAllPage(page);
        return ResponseEntity.ok(mapPageEntityToDto(pageEntidade));
    }

    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_READ))")
    @GetMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Obter os dados completos de uma entidiade pelo id informado!", responses = {
            @ApiResponse(responseCode = "200", description = "Entidade encontrada",
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
    public ResponseEntity<DTO> getById(
            @Parameter(description = "Id da entidade")
            @PathVariable("id") TYPE_PK id
    ) {
        DTO dtoResult = mapper.toDTO(service.getById(id));
        return ResponseEntity.ok(dtoResult);
    }

    @PreAuthorize(value = "hasRole(#root.this.getRoleName(#root.this.ROLE_DELETE))")
    @DeleteMapping(path ="/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Método utilizado para remover uma entidiade pela id informado", responses = {
            @ApiResponse(responseCode = "200", description = "Entidade Removida",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    public ResponseEntity<DTO> remove(
            @PathVariable("id") TYPE_PK id
    ) {
        DTO dtoResult = mapper.toDTO(service.deleteById(id));
        return ResponseEntity.ok(dtoResult);
    }

    @Override
    public String getRoleName(ISecurityRole role){
        return "ROLE_".concat(getEntityTypeSimpleName().toUpperCase().concat("_"+role.getName().toUpperCase()));
    }

    @Override
    public String getEntityTypeSimpleName() {
        return this.service.getEntityType().getSimpleName();
    }

    /**
     * Nome do modulo de Segurança, obtido a partir do nome da classe de serviço sem o Sufixo Service
     * @return - nome do modulo de segurança, utilizado para inicializar o cadastro do modulo de segurança
     */
    @Override
    public String getSecurityModuleName(){
        String simpleName = ClassUtils.getUserClass(this.service.getClass()).getSimpleName();
        simpleName = simpleName.replaceAll("Service","").replaceAll("Impl","");;
        return simpleName;
    };

    /**
     * Rotulo do modulo de Segurança, obtido a partir do nome da classe de serviço sem o Sufixo Service
     * @return - Rotulo do modulo de segurança, utilizado para inicializar o cadastro do modulo de segurança
     */
    @Override
    public String getSecurityModuleLabel(){
        String simpleName = ClassUtils.getUserClass(this.service.getClass()).getSimpleName();
        simpleName = simpleName.replaceAll("Service","").replaceAll("Impl","");
        return simpleName;
    };


    /**
     * retorna as roles sem a palavra ROLE, para alimentar o banco de autorização
     * @return
     */
    @Override
    public List<ISecurityRole> getSecurityModuleFeatures(){
        return ReflectionUtils.getRoleConstantFromController(this);
    }

    public Page<DTOList> mapPageEntityToDto(Page<MODEL> page){
        Page<DTOList> dtoPage = page.map(entity -> mapper.toDTOList(entity));
        return dtoPage;
    }

    @GetMapping(path = "/search-fields",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Listagem dos campos de busca", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem geral",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Modelo não parametrizado para pesquisa",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    public ResponseEntity<List<SearchField>> searchFieldsList(){
        List<SearchField> listSearchFields = service.listSearchFields();
        if(listSearchFields.isEmpty()){
            throw new BusinessException(ApiMessageCode.ERROR_SEARCH_PARAMETERS_NOT_DEFINED);
        }
        return ResponseEntity.ok(listSearchFields);
    }

    @PostMapping(path = "/search-fields",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Realiza a busca pelos valores dos campos informados", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem do resultado",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "falha ao realizar a busca",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    public ResponseEntity<List<DTOList>> searchFieldsAction(@RequestBody List<SearchFieldValue> searchFieldValues){
        List<MODEL> listSearchFields = service.searchFieldValues(searchFieldValues);
        if(listSearchFields.isEmpty()){
            throw new BusinessException(ApiMessageCode.SEARCH_FIELDS_RESULT_NONE);
        }
        return ResponseEntity.ok(mapper.fromModelToDTOList(listSearchFields));
    }

    @PostMapping(path = "/search-fields/page",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(description = "Realiza a busca pelos valores dos campos informados", responses = {
            @ApiResponse(responseCode = "200", description = "Listagem do resultado",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "falha ao realizar a busca",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "403", description = "Acesso negado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MessageResponse.class)))
    })
    public ResponseEntity<Page<DTOList>> searchFieldsActionPage(
            @RequestBody List<SearchFieldValue> searchFieldValues,
            @RequestParam(name = "page", defaultValue = "0", required = false)  Integer page,
            @RequestParam(name = "size", defaultValue = "5", required = false)  Integer size,
            @RequestParam(name = "sort", defaultValue = "", required = false)  List<String> sort
    ){
        Sort sortObject = Sort.unsorted();
        if(Objects.nonNull(sort)){
            List<Sort.Order> orderList = new ArrayList<>();
            sort.forEach(s -> orderList.add(Sort.Order.asc(s)));
            sortObject = Sort.by(orderList);
        }
        Pageable pageable = PageRequest.of(page, size, sortObject);
        Page<MODEL> listSearchFields = service.searchFieldValuesPage(pageable, searchFieldValues);
        if(listSearchFields.isEmpty()){
            throw new BusinessException(ApiMessageCode.SEARCH_FIELDS_RESULT_NONE);
        }
        return ResponseEntity.ok(this.mapPageEntityToDto(listSearchFields));
    }
}
