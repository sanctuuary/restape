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
public class Workflow {
    @Id
    @Column
    private int id;

    @Column
    private String domainName;

    @Column
    private String synthesisRunID;

    @Column
    private String cwlFilePath;

    @Column
    private String pngFilePath;

}