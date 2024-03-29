DROP TABLE IF EXISTS DOMAIN;  
CREATE TABLE DOMAIN (  
PK_domain_id INT AUTO_INCREMENT  PRIMARY KEY,  
domain_name VARCHAR(50) NOT NULL,  
domain_config_url VARCHAR(50) NOT NULL  
);  

DROP TABLE IF EXISTS SYNTHESIS_RUN;  
CREATE TABLE SYNTHESIS_RUN (  
PK_run_id INT AUTO_INCREMENT  PRIMARY KEY,  
domain_name VARCHAR(50) NOT NULL   -- should be changed to FK to DOMAIN once the table is populated
); 

DROP TABLE IF EXISTS WORKFLOW;  
CREATE TABLE WORKFLOW (  
PK_workflow_id INT AUTO_INCREMENT  PRIMARY KEY,  
workflow_name VARCHAR(50) NOT NULL,
workflow_number INT NOT NULL,
FK_synth_run_id int NOT NULL,
FOREIGN KEY (FK_synth_run_id) REFERENCES SYNTHESIS_RUN(PK_run_id),
cwl_path VARCHAR(50) NOT NULL,
png_path VARCHAR(50) NOT NULL
); 