package EasySQL;


public class Column {
    private String columnName;
    private String columnType;
    private String charset;

    public Column(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public Column(String columnName, String columnType, String charset) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
    }
    
    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
   
    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType.toUpperCase();
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
    
    public String toString(){
        return columnName + " " + columnType;
    }
}
