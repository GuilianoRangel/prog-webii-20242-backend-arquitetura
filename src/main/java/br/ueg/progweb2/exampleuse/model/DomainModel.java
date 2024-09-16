package br.ueg.progweb2.exampleuse.model;

import br.ueg.progweb2.arquitetura.model.GenericModel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Getter
@Setter
@ToString
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "DOMAIN_MODEL")
public class DomainModel implements GenericModel<Long> {
    @Id
    @SequenceGenerator(
            name="task_sequence",
            sequenceName = "task_sequence_bd",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = SEQUENCE,
            generator = "task_sequence"
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "descricao",  nullable = false, length = 200)
    private String description;

    @Column(name = "ativo")
    private Boolean active;
}
