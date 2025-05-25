package liquibase.diff.output.changelog.core;

import liquibase.Scope;
import liquibase.change.Change;
import liquibase.change.core.AddForeignKeyConstraintChange;
import liquibase.database.Database;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.AbstractChangeGenerator;
import liquibase.diff.output.changelog.ChangeGeneratorChain;
import liquibase.diff.output.changelog.MissingObjectChangeGenerator;
import liquibase.logging.Logger;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.*;
import liquibase.util.StringUtil;

public class MissingForeignKeyChangeGenerator extends AbstractChangeGenerator implements MissingObjectChangeGenerator {
    @Override
    public int getPriority(Class<? extends DatabaseObject> objectType, Database database) {
        if (ForeignKey.class.isAssignableFrom(objectType)) {
            return PRIORITY_DEFAULT;
        }
        return PRIORITY_NONE;
    }

    @Override
    public Class<? extends DatabaseObject>[] runAfterTypes() {
        return new Class[] {
                Table.class,
                Column.class,
                PrimaryKey.class,
                UniqueConstraint.class,
                Index.class
        };
    }

    @Override
    public Class<? extends DatabaseObject>[] runBeforeTypes() {
        return null;
    }

    @Override
    public Change[] fixMissing(DatabaseObject missingObject, DiffOutputControl control, Database referenceDatabase, Database comparisonDatabase, ChangeGeneratorChain chain) {
        ForeignKey fk = (ForeignKey) missingObject;

        AddForeignKeyConstraintChange change = new AddForeignKeyConstraintChange();
        change.setConstraintName(fk.getName());

        String defaultSchemaName = StringUtil.trimToEmpty(referenceDatabase.getDefaultSchemaName());
        String defaultCatalogName = StringUtil.trimToEmpty(referenceDatabase.getDefaultCatalogName());

        String compDefaultSchemaName = StringUtil.trimToEmpty(comparisonDatabase.getDefaultSchemaName());
        String compDefaultCatalogName = StringUtil.trimToEmpty(comparisonDatabase.getDefaultCatalogName());

        boolean includedCatalog = false;
        change.setReferencedTableName(fk.getPrimaryKeyTable().getName());

        String missingPrimaryKeyCatalogName = StringUtil.trimToEmpty(fk.getPrimaryKeyTable().getSchema().getCatalogName());
        if (referenceDatabase.supports(Catalog.class)) {
            if (control.getIncludeCatalog() || control.considerCatalogsAsSchemas()) {
                change.setReferencedTableCatalogName(fk.getPrimaryKeyTable().getSchema().getCatalogName());
                includedCatalog = true;
            } else if (!defaultCatalogName.equalsIgnoreCase(missingPrimaryKeyCatalogName)) {
                if (!compDefaultCatalogName.equalsIgnoreCase(missingPrimaryKeyCatalogName)) { //don't include catalogName if it's in the default catalog
                    change.setReferencedTableCatalogName(fk.getPrimaryKeyTable().getSchema().getCatalogName());
                    includedCatalog = true;
                }
            }
        }

        String missingPrimaryKeySchemaName = StringUtil.trimToEmpty(fk.getPrimaryKeyTable().getSchema().getName());
        if (referenceDatabase.supports(Schema.class)) {
            if (includedCatalog || control.getIncludeSchema()) {
                change.setReferencedTableSchemaName(fk.getPrimaryKeyTable().getSchema().getName());
            } else if (!defaultSchemaName.equalsIgnoreCase(missingPrimaryKeySchemaName)) {
                if (!compDefaultSchemaName.equalsIgnoreCase(missingPrimaryKeySchemaName)) { //don't include schemaName if it's in the default schema
                    change.setReferencedTableSchemaName(fk.getPrimaryKeyTable().getSchema().getName());
                }
            }
        }

        // Check if primary key columns are empty and log a warning if they are
        if (fk.getPrimaryKeyColumns() == null || fk.getPrimaryKeyColumns().isEmpty()) {
            Logger log = Scope.getCurrentScope().getLog(MissingForeignKeyChangeGenerator.class);
            log.warning(String.format("Foreign key '%s' on table '%s' references table '%s' which may not exist or has no primary key columns. " +
                    "This will result in empty referencedColumnNames in the generated changelog. " +
                    "Please verify that the referenced table exists and has a primary key defined.",
                    fk.getName() != null ? fk.getName() : "<unnamed>",
                    fk.getForeignKeyTable() != null ? fk.getForeignKeyTable().getName() : "<unknown>",
                    fk.getPrimaryKeyTable() != null ? fk.getPrimaryKeyTable().getName() : "<unknown>"));
        }

        change.setReferencedColumnNames(StringUtil.join(fk.getPrimaryKeyColumns(), ",", (StringUtil.StringUtilFormatter<Column>) Column::getName));

        change.setBaseTableName(fk.getForeignKeyTable().getName());
        if (control.getIncludeCatalog()) {
            change.setBaseTableCatalogName(fk.getForeignKeyTable().getSchema().getCatalogName());
        }
        if (control.getIncludeSchema()) {
            change.setBaseTableSchemaName(fk.getForeignKeyTable().getSchema().getName());
        }
        change.setBaseColumnNames(StringUtil.join(fk.getForeignKeyColumns(), ",", (StringUtil.StringUtilFormatter<Column>) Column::getName));

        change.setDeferrable(fk.isDeferrable());
        change.setInitiallyDeferred(fk.isInitiallyDeferred());
        change.setValidate(fk.shouldValidate());
        change.setOnUpdate(fk.getUpdateRule());
        change.setOnDelete(fk.getDeleteRule());

        Index backingIndex = fk.getBackingIndex();
        if (referenceDatabase.createsIndexesForForeignKeys()) {
            control.setAlreadyHandledMissing(backingIndex);
        }

        return new Change[] { change };
    }
}
