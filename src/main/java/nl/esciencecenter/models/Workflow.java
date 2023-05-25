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
/**
 * Class representing a workflow generated by APE and stored in the filesystem.
 */
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

    @Column
    private String structureJSON;

}