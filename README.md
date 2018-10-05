# SolverCheck 
---

[![Maven Central](https://img.shields.io/maven-central/v/org.bitbucket.xaviergillard/SolverCheck.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.bitbucket.xaviergillard%22%20AND%20a:%22SolverCheck%22)
[![Javadocs](https://www.javadoc.io/badge/org.bitbucket.xaviergillard/SolverCheck.svg)](https://www.javadoc.io/doc/org.bitbucket.xaviergillard/SolverCheck)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Bitbucket Pipelines](https://img.shields.io/bitbucket/pipelines/xaviergillard/solvercheck.svg)](https://bitbucket.org/xaviergillard/solvercheck/src/master/)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4f6250ef42444390aa30619e10193e64)](https://www.codacy.com/app/xaviergillard/solvercheck?utm_source=xaviergillard@bitbucket.org&amp;utm_medium=referral&amp;utm_content=xaviergillard/solvercheck&amp;utm_campaign=Badge_Grade)
[![codecov](https://codecov.io/bb/xaviergillard/solvercheck/branch/master/graph/badge.svg)](https://codecov.io/bb/xaviergillard/solvercheck)


SolverCheck is a library to test the implementation of constraints in CP solvers.

---  

## Installation
### Using Maven
If you're using Maven, it suffices that you add the following dependency to 
your pom.xml
```
<dependency>
  <groupId>org.bitbucket.xaviergillard</groupId>
  <artifactId>SolverCheck</artifactId>
  <version>1.0.0</version>
  <scope>test</scope>
</dependency>
``` 

### Manual installation
If you're not using Maven (you should definitely give it a try !), you can 
still use `SolverCheck` as it is a plain dependency (.jar) which you can add 
to your classpath. To do so, you should: 
* Download the jar from [here](https://repo1.maven.org/maven2/org/bitbucket/xaviergillard/SolverCheck/1.0.0/SolverCheck-1.0.0.jar)
* Add it to your classpath `java -cp <your cp including solvercheck> 
your_application`

## Documentation
The complete javadoc of the project available 
[here](http://javadoc.io/doc/org.bitbucket.xaviergillard/SolverCheck/1.0.0). 
Alternatively, the complete maven-site with all javadoc and quality assurance 
reports can be downloaded from [here](https://bitbucket.org/xaviergillard/solvercheck/downloads/SolverCheck-1.0.0.qa_site.tgz).

## Quality Assurance
A high amount of effort has been paid to write `SolverCheck` in a clear 
(intelligible) and correct way. Additionally, some tools are used to 
automatically assess the quality of `SolverCheck`'s codebase as well as the 
quality of the tests it generates. 

Practically, the following tools are used:
* `Checkstyle` to enforce a consistent coding standard.
* `Spotbugs` to statically detect frequently occurring bug patterns.
* `JaCoCo` to compute the **branch** coverage of the test base.
* `PIT` to compute the **mutations** coverage of the test base.

The complete maven-site with all javadoc and quality assurance reports can be 
downloaded [here](https://bitbucket.org/xaviergillard/solvercheck/downloads/SolverCheck-1.0.0.qa_site.tgz).

## Releases
### 1.0.0
* Initial version of the tool, provides all the necessary tooling.

##### TODO:
* [ ] Pruners/non-monotonic stuff
* [ ] Write the rest of the Readme
* [ ] Write examples
* [ ] Assess quality of known solvers
* [ ] Scheduling
* [ ] Write paper.

