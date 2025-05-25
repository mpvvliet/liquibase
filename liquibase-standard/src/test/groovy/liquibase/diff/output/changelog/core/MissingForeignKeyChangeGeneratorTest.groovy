package liquibase.diff.output.changelog.core

import liquibase.Scope
import liquibase.change.core.AddForeignKeyConstraintChange
import liquibase.database.core.MockDatabase
import liquibase.diff.output.DiffOutputControl
import liquibase.diff.output.changelog.ChangeGeneratorChain
import liquibase.logging.core.BufferedLogService
import liquibase.structure.core.*
import spock.lang.Specification

class MissingForeignKeyChangeGeneratorTest extends Specification {

    def "fixMissing logs warning when foreign key has no primary key columns"() {
        given:
        def generator = new MissingForeignKeyChangeGenerator()
        def control = new DiffOutputControl()
        def database = new MockDatabase()

        // Create a foreign key with empty primary key columns (simulating non-existent referenced table)
        def foreignKeyTable = new Table("test_catalog", "test_schema", "foreign_table")
        def primaryKeyTable = new Table("test_catalog", "test_schema", "primary_table")

        def fk = new ForeignKey("fk_test")
        fk.setForeignKeyTable(foreignKeyTable)
        fk.setPrimaryKeyTable(primaryKeyTable)

        // Add foreign key columns but leave primary key columns empty
        fk.addForeignKeyColumn(new Column("foreign_col").setRelation(foreignKeyTable))
        // Intentionally not adding primary key columns to simulate the issue

        // Set up buffered logging to capture warning messages
        def bufferedLogService = new BufferedLogService()

        when:
        def changes = Scope.child([(Scope.Attr.logService.name()): bufferedLogService], {
            return generator.fixMissing(fk, control, database, database, new ChangeGeneratorChain(null))
        } as Scope.ScopedRunnerWithReturn)

        then:
        changes.length == 1
        changes[0] instanceof AddForeignKeyConstraintChange

        def change = (AddForeignKeyConstraintChange) changes[0]
        change.getConstraintName() == "fk_test"
        change.getBaseTableName() == "foreign_table"
        change.getReferencedTableName() == "primary_table"
        change.getReferencedColumnNames() == "" // This should be empty due to no primary key columns

        // Check that a warning was logged
        def logMessages = bufferedLogService.getLog()
        logMessages.size() > 0
        def warningMessage = logMessages.find { it.level.getName() == "WARNING" }
        warningMessage != null
        warningMessage.message.contains("Foreign key 'fk_test'")
        warningMessage.message.contains("references table 'primary_table'")
        warningMessage.message.contains("may not exist or has no primary key columns")
        warningMessage.message.contains("empty referencedColumnNames")
    }

    def "fixMissing does not log warning when foreign key has primary key columns"() {
        given:
        def generator = new MissingForeignKeyChangeGenerator()
        def control = new DiffOutputControl()
        def database = new MockDatabase()

        // Create a foreign key with proper primary key columns
        def foreignKeyTable = new Table("test_catalog", "test_schema", "foreign_table")
        def primaryKeyTable = new Table("test_catalog", "test_schema", "primary_table")

        def fk = new ForeignKey("fk_test")
        fk.setForeignKeyTable(foreignKeyTable)
        fk.setPrimaryKeyTable(primaryKeyTable)

        // Add both foreign key and primary key columns
        fk.addForeignKeyColumn(new Column("foreign_col").setRelation(foreignKeyTable))
        fk.addPrimaryKeyColumn(new Column("primary_col").setRelation(primaryKeyTable))

        // Set up buffered logging to capture any messages
        def bufferedLogService = new BufferedLogService()

        when:
        def changes = Scope.child([(Scope.Attr.logService.name()): bufferedLogService], {
            return generator.fixMissing(fk, control, database, database, new ChangeGeneratorChain(null))
        } as Scope.ScopedRunnerWithReturn)

        then:
        changes.length == 1
        changes[0] instanceof AddForeignKeyConstraintChange

        def change = (AddForeignKeyConstraintChange) changes[0]
        change.getConstraintName() == "fk_test"
        change.getBaseTableName() == "foreign_table"
        change.getReferencedTableName() == "primary_table"
        change.getReferencedColumnNames() == "primary_col" // This should have the column name

        // Check that no warning was logged
        def logMessages = bufferedLogService.getLog()
        def warningMessages = logMessages.findAll { it.level.getName() == "WARNING" }
        warningMessages.size() == 0
    }

    def "fixMissing handles null foreign key name gracefully"() {
        given:
        def generator = new MissingForeignKeyChangeGenerator()
        def control = new DiffOutputControl()
        def database = new MockDatabase()

        // Create a foreign key with null name and empty primary key columns
        def foreignKeyTable = new Table("test_catalog", "test_schema", "foreign_table")
        def primaryKeyTable = new Table("test_catalog", "test_schema", "primary_table")

        def fk = new ForeignKey()  // No name provided
        fk.setForeignKeyTable(foreignKeyTable)
        fk.setPrimaryKeyTable(primaryKeyTable)
        fk.addForeignKeyColumn(new Column("foreign_col").setRelation(foreignKeyTable))

        // Set up buffered logging to capture warning messages
        def bufferedLogService = new BufferedLogService()

        when:
        def changes = Scope.child([(Scope.Attr.logService.name()): bufferedLogService], {
            return generator.fixMissing(fk, control, database, database, new ChangeGeneratorChain(null))
        } as Scope.ScopedRunnerWithReturn)

        then:
        changes.length == 1

        // Check that warning was logged with "<unnamed>" placeholder
        def logMessages = bufferedLogService.getLog()
        def warningMessage = logMessages.find { it.level.getName() == "WARNING" }
        warningMessage != null
        warningMessage.message.contains("Foreign key '<unnamed>'")
    }
}
