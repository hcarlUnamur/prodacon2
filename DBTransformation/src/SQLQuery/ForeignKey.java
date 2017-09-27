package SQLQuery;

public class ForeignKey {
    
    private String tableName;
    private String ReferencedColumn;
    private String foreingKeyColumn;
    private String constraintName;

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public ForeignKey() {
        this.tableName = null;
        this.ReferencedColumn = null;
        this.foreingKeyColumn = null;
    }

    public ForeignKey(String tableName, String ReferencedColumn, String foreingKeyColumn, String constraintName) {
        this.tableName = tableName;
        this.ReferencedColumn = ReferencedColumn;
        this.foreingKeyColumn = foreingKeyColumn;
        this.constraintName = constraintName;
    }
    
    
        
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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
