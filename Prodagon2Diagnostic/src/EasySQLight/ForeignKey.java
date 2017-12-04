package EasySQLight;

import Tools.Jsonable;
import java.util.Objects;

public class ForeignKey implements Jsonable{
    
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
        if (!Objects.equals(this.ReferencedTableName.toUpperCase(), other.ReferencedTableName.toUpperCase())) {
            return false;
        }
        if (!Objects.equals(this.ReferencedColumn.toUpperCase(), other.ReferencedColumn.toUpperCase())) {
            return false;
        }
        if (!Objects.equals(this.foreingKeyColumn.toUpperCase(), other.foreingKeyColumn.toUpperCase())) {
            return false;
        }
        if (!Objects.equals(this.foreingKeyTable.toUpperCase(), other.foreingKeyTable.toUpperCase())) {
            return false;
        }
        if (!Objects.equals(this.constraintName.toUpperCase(), other.constraintName.toUpperCase())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ForeignKey{" + "ReferencedTableName=" + ReferencedTableName + ", ReferencedColumn=" + ReferencedColumn + ", foreingKeyColumn=" + foreingKeyColumn + ", foreingKeyTable=" + foreingKeyTable + ", constraintName=" + constraintName + '}';
    }

    @Override
    protected ForeignKey clone() throws CloneNotSupportedException {
        return new ForeignKey(ReferencedTableName, ReferencedColumn, foreingKeyColumn, foreingKeyTable, constraintName);
    }
    
    public String toJson(){
        return "{" + "\"ReferencedTableName\":\"" + ReferencedTableName + "\", \"ReferencedColumn\":\"" + ReferencedColumn + "\", \"ForeingKeyColumn\":\"" + foreingKeyColumn + "\", \"ForeingKeyTable\":\"" + foreingKeyTable + "\", \"ConstraintName\":\"" + constraintName + "\"}";
    }
    
    
}
