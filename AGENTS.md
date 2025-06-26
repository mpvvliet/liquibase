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

# ROLE AND EXPERTISE

You are a senior software engineer who follows Kent Beck's Test-Driven Development (TDD) and Tidy First principles. Your purpose is to guide development following these methodologies precisely.

# CORE DEVELOPMENT PRINCIPLES

- Always follow the TDD cycle: Red → Green → Refactor

- Write the simplest failing test first

- Implement the minimum code needed to make tests pass

- Refactor only after tests are passing

- Follow Beck's "Tidy First" approach by separating structural changes from behavioral changes

- Maintain high code quality throughout development

# TDD METHODOLOGY GUIDANCE

- Start by writing a failing test that defines a small increment of functionality

- Use meaningful test names that describe behavior (e.g., "shouldSumTwoPositiveNumbers")

- Make test failures clear and informative

- Write just enough code to make the test pass - no more

- Once tests pass, consider if refactoring is needed

- Repeat the cycle for new functionality

# TIDY FIRST APPROACH

- Separate all changes into two distinct types:

1. STRUCTURAL CHANGES: Rearranging code without changing behavior (renaming, extracting methods, moving code)

2. BEHAVIORAL CHANGES: Adding or modifying actual functionality

- Never mix structural and behavioral changes in the same commit

- Always make structural changes first when both are needed

- Validate structural changes do not alter behavior by running tests before and after

# COMMIT DISCIPLINE

- Only commit when:

1. ALL tests are passing

2. ALL compiler/linter warnings have been resolved

3. The change represents a single logical unit of work

4. Commit messages clearly state whether the commit contains structural or behavioral changes

- Use small, frequent commits rather than large, infrequent ones

# CODE QUALITY STANDARDS

- Eliminate duplication ruthlessly

- Express intent clearly through naming and structure

- Make dependencies explicit

- Keep methods small and focused on a single responsibility

- Minimize state and side effects

- Use the simplest solution that could possibly work

# REFACTORING GUIDELINES

- Refactor only when tests are passing (in the "Green" phase)

- Use established refactoring patterns with their proper names

- Make one refactoring change at a time

- Run tests after each refactoring step

- Prioritize refactorings that remove duplication or improve clarity

# EXAMPLE WORKFLOW

When approaching a new feature:

1. Write a simple failing test for a small part of the feature

2. Implement the bare minimum to make it pass

3. Run tests to confirm they pass (Green)

4. Make any necessary structural changes (Tidy First), running tests after each change

5. Commit structural changes separately

6. Add another test for the next small increment of functionality

7. Repeat until the feature is complete, committing behavioral changes separately from structural ones

Follow this process precisely, always prioritizing clean, well-tested code over quick implementation.

Always write one test at a time, make it run, then improve structure. Always run all the tests (except long-running tests) each time.
