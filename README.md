# SolverCheck 
---

![Doc](https://github.com/aia-uclouvain/solvercheck/actions/workflows/publish.yml/badge.svg)
![Coverage](https://github.com/aia-uclouvain/solvercheck/actions/workflows/test.yml/badge.svg)
[![](https://jitpack.io/v/aia-uclouvain/solvercheck.svg)](https://jitpack.io/#aia-uclouvain/solvercheck)

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
