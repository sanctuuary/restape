![RestAPE Logo](https://user-images.githubusercontent.com/11068408/225042915-416975d6-56c9-40d3-97b9-e2854cc19a1c.png#gh-dark-mode-only)
![RestAPE Logo](https://user-images.githubusercontent.com/11068408/225042428-824741e2-9618-413c-9546-bc352b3bb23b.png#gh-light-mode-only)


| Badges | |
|:----:|----|
| **Fairness** |  [![fair-software.eu](https://img.shields.io/badge/fair--software.eu-%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F-green)](https://fair-software.eu) [![OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/projects/8082/badge)](https://www.bestpractices.dev/projects/8082) |
| **Packages and Releases** |  [![Latest release](https://img.shields.io/github/release/sanctuuary/RESTAPE.svg)](https://github.com/sanctuuary/APE/releases/latest) [![Static Badge](https://img.shields.io/badge/RSD-RESTfulAPE-ape)](https://research-software-directory.org/software/restape) |
| **Build Status** | ![build](https://github.com/sanctuuary/RestAPE/actions/workflows/maven.yml/badge.svg) [![CodeQL](https://github.com/sanctuuary/restape/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanctuuary/restape/actions/workflows/codeql.yml) |
| **Documentation** | [![Documentation Status](https://readthedocs.org/projects/ape-framework/badge/?version=latest)](https://ape-framework.readthedocs.io/en/latest/docs/restful-ape/introduction.html) |
| **DOI** | [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10048236.svg)](https://doi.org/10.5281/zenodo.10048236) |
| **License** |  [![GitHub license](https://img.shields.io/github/license/sanctuuary/RESTAPE)](https://github.com/sanctuuary/RESTAPE/blob/master/LICENSE) |



A RESTful API for the APE library (RESTful APE) provides a way for users to interact with APE's automated pipeline exploration capabilities through HTTP requests. APE is a command line tool and Java API that automates the exploration of possible computational pipelines from large collections of computational tools.

The RESTful API allows users to submit requests to the APE server for pipeline exploration, which returns results in a standard format such as JSON or XML. Users can interact with APE through a web browser or any other HTTP client, and the API can be integrated into other applications for seamless pipeline exploration.

Overall, the RESTful API for APE provides a powerful and flexible way for users to leverage APE's capabilities in their scientific workflows.

## Development

RESTful API for the APE library, based on Spring Boot.

To run the Spring Boot directly, you can run the following cmd:

`mvn spring-boot:run`

Alternatively, you can build the jar package

`mvn clean package`

and then run the jar package

`java -jar target/restape-[version].jar`



OpenAPI documentation is available at
````
[host]:[port]]/swagger-ui/index.html
````

#### Use local APE version

If the APE version you wish to use is not available on the [Mvn repository](https://mvnrepository.com/artifact/io.github.sanctuuary/APE)
you can install APE in your local repository and use it to build the back-end.
To do so, [download](https://github.com/sanctuuary/APE#releases)
or [compile](https://github.com/sanctuuary/APE#build-ape-from-source-using-maven) the APE version you wish to use.
In the location where you have the resulting APE.jar file, run the following command:
````shell
$ mvn install:install-file -Dfile=APE-<version>.jar
````
This adds the specified APE file to your local Maven repository.
You can now build the back-end using:
````shell
$ mvn package -DskipTests=true
````
