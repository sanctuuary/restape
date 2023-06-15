package nl.esciencecenter.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class DomainSynthesisCashe {
    @Id
    @Column
    private int id;

    @Column
    private int domainID;

    @Column
    private int solutionLength;

    @Column
    private String cashedFilePath;

}