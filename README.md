![RestAPE Logo](https://user-images.githubusercontent.com/11068408/225042915-416975d6-56c9-40d3-97b9-e2854cc19a1c.png#gh-dark-mode-only)
![RestAPE Logo](https://user-images.githubusercontent.com/11068408/225042428-824741e2-9618-413c-9546-bc352b3bb23b.png#gh-light-mode-only)

![build](https://github.com/sanctuuary/RestAPE/actions/workflows/maven.yml/badge.svg)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


RESTful API for the APE library, based on Spring Boot.



To run the Spring Boot directly, you can run the following cmd:

`mvn spring-boot:run`

Alternatively, you can build the jar package

`mvn clean package`

and then run the jar package

`java -jar target/restape-[version].jar`



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