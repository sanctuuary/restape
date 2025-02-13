<img src="https://raw.githubusercontent.com/sanctuuary/.github/main/logos/RESTful-APE-dark-logo.png#gh-dark-mode-only" alt="RestAPE Logo" style="width: 80%;">
<img src="https://raw.githubusercontent.com/sanctuuary/.github/main/logos/RESTful-light-logo.png#gh-light-mode-only" alt="RestAPE Logo" style="width: 80%;">

| Badges | |
|:----:|----|
| **Fairness** |  [![fair-software.eu](https://img.shields.io/badge/fair--software.eu-%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F%20%20%E2%97%8F-green)](https://fair-software.eu) [![OpenSSF Best Practices](https://bestpractices.coreinfrastructure.org/projects/8082/badge)](https://www.bestpractices.dev/projects/8082) |
| **Packages and Releases** |  [![Latest release](https://img.shields.io/github/release/sanctuuary/RESTAPE.svg)](https://github.com/sanctuuary/restape/releases/latest) [![Static Badge](https://img.shields.io/badge/RSD-RESTfulAPE-ape)](https://research-software-directory.org/software/restape) | 
| **Build Status** | ![build](https://github.com/sanctuuary/RestAPE/actions/workflows/maven.yml/badge.svg) [![CodeQL](https://github.com/sanctuuary/restape/actions/workflows/codeql.yml/badge.svg)](https://github.com/sanctuuary/restape/actions/workflows/codeql.yml) |
| **Documentation** | [![Documentation Status](https://readthedocs.org/projects/ape-framework/badge/?version=latest)](https://ape-framework.readthedocs.io/en/latest/docs/restful-ape/introduction.html) |
| **DOI** | [![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.10048236.svg)](https://doi.org/10.5281/zenodo.10048236) |
| **License** |  [![GitHub license](https://img.shields.io/github/license/sanctuuary/RESTAPE)](https://github.com/sanctuuary/RESTAPE/blob/master/LICENSE) |

The RESTful API for the APE library (RESTful APE) allows users to interact with APE's automated pipeline exploration features through HTTP requests. APE automates the exploration of computational pipelines from large collections of computational tools.

Users can submit pipeline exploration requests to the APE server and receive results in JSON format. This interface allows interaction with APE via web browsers or any HTTP client and can be integrated into other applications.

In addition to APE's core feature of automated workflow composition, the API performs design-time benchmarking of workflows by aggregating tool-specific information such as licenses and citations for better workflow comparison.

RESTful APE is packaged in a [Docker image](https://github.com/sanctuuary/restape/pkgs/container/restape).

Overall, the RESTful API for APE provides a flexible way to leverage APE's capabilities in scientific workflows.

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

### Configure .env file

To configure the application, you can create a `.env` file in the root directory of the project. See the following content as an example:

```
REST_APE_PORT = 4444
PUBMETRIC_ENDPOINT= localhost
PUBMETRIC_PORT = 8000
```

We use `REST_APE_PORT` to specify the port on which the application will run. When run locally under `REST_APE_PORT=4444` the service would be available on `localhost:4444`.
The `PUBMETRIC_ENDPOINT` and `PUBMETRIC_PORT` are used to specify the endpoint and port of the Pubmetric service that is used to retrieve tool metrics.

#### Use local APE version

If the APE version you wish to use is not available on the [Mvn repository](https://mvnrepository.com/artifact/io.github.sanctuuary/APE)
you can install APE in your local repository and use it to build the back-end.
To do so, [download](https://github.com/sanctuuary/APE#releases)
or [compile](https://github.com/sanctuuary/APE#build-ape-from-source-using-maven) the APE version you wish to use.
In the location where you have the resulting APE.jar file, run the following command:

````shell
mvn install:install-file -Dfile=APE-<version>.jar
````

This adds the specified APE file to your local Maven repository.
You can now build the back-end using:

````shell
mvn package -DskipTests=true
````
