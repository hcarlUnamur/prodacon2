package EasySQL;

public class ForeignKey {
    
    private String ReferencedTableName;
    private String ReferencedColumn;
    private String foreingKeyColumn;
    private String constraintName;

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
    
    
}
