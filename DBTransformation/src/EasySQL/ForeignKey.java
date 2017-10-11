package EasySQL;

import java.util.Objects;

public class ForeignKey {
    
    private String ReferencedTableName;
    private String ReferencedColumn;
    private String foreingKeyColumn;
    private String foreingKeyTable;
    private String constraintName;

    public ForeignKey(String ReferencedTableName, String ReferencedColumn, String foreingKeyColumn, String foreingKeyTable, String constraintName) {
        this.ReferencedTableName = ReferencedTableName;
        this.ReferencedColumn = ReferencedColumn;
        this.foreingKeyColumn = foreingKeyColumn;
        this.foreingKeyTable = foreingKeyTable;
        this.constraintName = constraintName;
    }

    public String getForeingKeyTable() {
        return foreingKeyTable;
    }

    public void setForeingKeyTable(String foreingKeyTable) {
        this.foreingKeyTable = foreingKeyTable;
    }

    public String getReferencedTableName() {
        return ReferencedTableName;
    }

    public void setReferencedTableName(String ReferencedTableName) {
        this.ReferencedTableName = ReferencedTableName;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public ForeignKey() {
        this.ReferencedTableName = null;
        this.ReferencedColumn = null;
        this.foreingKeyColumn = null;
        this.constraintName = null;
    }

    public ForeignKey(String tableReferencedName, String ReferencedColumn, String foreingKeyColumn, String constraintName) {

        this.ReferencedTableName = tableReferencedName;
        this.ReferencedColumn = ReferencedColumn;
        this.foreingKeyColumn = foreingKeyColumn;
        this.constraintName = constraintName;
    }


    public String getReferencedColumn() {
        return ReferencedColumn;
    }

    public void setReferencedColumn(String ReferencedColumn) {
        this.ReferencedColumn = ReferencedColumn;
    }

    public String getForeingKeyColumn() {
        return foreingKeyColumn;
    }

    public void setForeingKeyColumn(String foreingKeyColumn) {
        this.foreingKeyColumn = foreingKeyColumn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ForeignKey other = (ForeignKey) obj;
        if (!Objects.equals(this.ReferencedTableName, other.ReferencedTableName)) {
            return false;
        }
        if (!Objects.equals(this.ReferencedColumn, other.ReferencedColumn)) {
            return false;
        }
        if (!Objects.equals(this.foreingKeyColumn, other.foreingKeyColumn)) {
            return false;
        }
        if (!Objects.equals(this.foreingKeyTable, other.foreingKeyTable)) {
            return false;
        }
        if (!Objects.equals(this.constraintName, other.constraintName)) {
            return false;
        }
        return true;
    }
    
    
}
