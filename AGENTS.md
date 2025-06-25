# AGENTS.md

## Overview

This document provides guidelines for AI agents and developers working with the Liquibase open-source project. It encompasses tools, testing protocols, coding standards, and best practices to ensure consistent and efficient contributions.

## Tools and Technologies

- **Build Tool**: Apache Maven
- **Programming Languages**: Java, Groovy
- **Testing Frameworks**: JUnit, Spock
- **Version Control**: Git
- **Database Support**: Multiple databases including PostgreSQL, MySQL, Oracle, etc.

## Building the Project

To compile and build the project, execute the following command in the root directory:

```bash
mvn clean install
```

## Running Tests

### Unit Tests

Unit tests are primarily written using the Spock framework and are located in the `src/test/groovy` directory.

Run all unit tests:

```bash
mvn test
```

### Integration Tests

Integration tests validate the interaction between Liquibase and external systems like databases. They are located in the `liquibase-integration-tests` module.

Run all integration tests:

```bash
mvn verify
```

Ensure that the necessary test databases are accessible and configured appropriately before running integration tests.

## Coding Conventions & Style Guide

- **Language**: Java (primary), Groovy (for tests)
- **Code Style**: Follow standard Java conventions. Use meaningful names, proper indentation, and Javadoc comments where necessary.
- **Testing**:
  - Write unit tests for all new features and bug fixes.
  - Use Spock for writing expressive and readable tests.
  - Place unit tests in `src/test/groovy` and integration tests in `liquibase-integration-tests/src/test/java`.
- **Changelogs**:
  - Use XML format for changelogs.
  - Store changelog files in the `src/main/resources` directory.
  - Ensure each changeset has a unique `id` and `author`.
- **Properties File**:
  - Create a `liquibase.properties` file in `src/main/resources` to store database connection details and other configurations.

## Database Configuration

Configure database connection details in the `liquibase.properties` file:

```properties
changeLogFile=src/main/resources/dbchangelog.xml
url=jdbc:postgresql://localhost:5432/mydb
username=dbuser
password=dbpassword
driver=org.postgresql.Driver
```

Ensure the appropriate JDBC driver is included in the `pom.xml` dependencies.

## Common Maven Commands

- **Update Database**:

```bash
mvn liquibase:update
```

- **Generate SQL for Updates**:

```bash
mvn liquibase:updateSQL
```

- **Rollback Changes**:

```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

- **Generate Changelog from Existing Database**:

```bash
mvn liquibase:generateChangeLog
```

Note: The `generateChangeLog` command has limitations and may not capture all database objects like stored procedures or triggers.

## Contribution Guidelines

To contribute to the Liquibase project:

1. **Fork the Repository**: Create a personal fork of the Liquibase repository on GitHub.
2. **Clone the Fork**: Clone your fork to your local machine.
3. **Create a Branch**: Create a new branch for your feature or bug fix.
4. **Make Changes**: Implement your changes, ensuring code quality and test coverage.
5. **Run Tests**: Execute all tests to ensure nothing is broken.
6. **Commit and Push**: Commit your changes and push the branch to your fork.
7. **Submit Pull Request**: Open a pull request against the main Liquibase repository.

## Additional Resources

- **Liquibase Documentation**: https://docs.liquibase.com/
- **Liquibase GitHub Repository**: https://github.com/liquibase/liquibase
- **Community Forums**: https://forum.liquibase.org/
