# SolverCheck 
---

![Doc](https://github.com/aia-uclouvain/solvercheck/actions/workflows/publish.yml/badge.svg)
![Coverage](https://github.com/aia-uclouvain/solvercheck/actions/workflows/test.yml/badge.svg)
<!-- ![Test coverage](https://raw.githubusercontent.com/<username>/<repository>/badges/badges/<branch>/badge.svg) -->


SolverCheck is a library to test the implementation of constraints in CP solvers.


It was introduced in the following paper :

* Gillard, X., Schaus, P., & Deville, Y. (2019). SolverCheck: Declarative testing of constraints. In International Conference on Principles and Practice of Constraint Programming.
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

## Releases
### 1.0.0
* Initial version of the tool, provides all the necessary tooling.

