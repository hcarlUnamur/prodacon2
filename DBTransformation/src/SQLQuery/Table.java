/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SQLQuery;

import java.util.ArrayList;

/**
 *
 * @author carl_
 */
public class Table {

    private ArrayList<Column> Tablecolumn;
    private String name;

    public Table( String name, ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    public Table() {
        this.Tablecolumn = new ArrayList<Column>();
    }
    
    public Table(ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
    }

    public ArrayList<Column> getTablecolumn() {
        return Tablecolumn;
    }

    public void setTablecolumn(ArrayList<Column> Tablecolumn) {
        this.Tablecolumn = Tablecolumn;
    }
    
    public void addColumn(Column col){
        this.Tablecolumn.add(col);
    }
    
    public String[] toArray(){
        ArrayList<String> out = new ArrayList<String>();
        for( Column col : Tablecolumn){
            out.add(col.getColumnName()+" "+col.getColumnType());
        }
        return out.toArray(new String[out.size()]);
    }
    
}
