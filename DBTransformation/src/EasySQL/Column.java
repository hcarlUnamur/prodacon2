package EasySQL;


public class Column {
    private String columnName;
    private String columnType;
    private String charset;
    private String defaultValue;

    public Column(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public Column(String columnName, String columnType, String charset) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
    }

    public Column(String columnName, String columnType, String charset, String defaultValue) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
 
    public String getCharset() {
        return ((charset!=null)?charset.replace(" ", "").toLowerCase():null);
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
