package EasySQLight;

import Tools.Jsonable;


public class Column implements Jsonable{
    private String columnName;
    private String columnType;
    private String charset;
    private String defaultValue;
    private boolean unsigned;

    public Column(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public Column(String columnName, String columnType, String charset) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
    }

    public Column(String columnName, String columnType, String charset, String defaultValue, boolean unsigned) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
        this.defaultValue = defaultValue;
        this.unsigned = unsigned;
    }
    
    public Column(String columnName, String columnType, String charset, String defaultValue) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.charset = charset;
        this.defaultValue = defaultValue;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
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
    
    @Override
    public String toJson(){
        StringBuilder out = new StringBuilder();
        out.append("{");
        out.append("\"columnName\":"+"\""+this.columnName+"\",");
        out.append("\"columnType\":"+"\""+this.columnType+"\",");
        out.append("\"defaultValue\":"+"\""+this.defaultValue+"\",");
        out.append("\"charset\":"+"\""+((this.charset!=null)?(this.charset.replace("\"", "\\\"")):"null")+"\",");
        out.append("\"unsigned\":"+"\""+this.unsigned+"\"");
        out.append("}");
        return out.toString();
    }
}
