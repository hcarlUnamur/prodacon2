package DataStructures.Queries;

import java.io.Serializable;

/**
 *
 * @author carl_
 */
public class Call implements Serializable {
    
    private int beginLine;
    private int beginCol;
    private int endLine;
    private int endCol;
    private String path;

    public Call() {
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getBeginCol() {
        return beginCol;
    }

    public void setBeginCol(int beginCol) {
        this.beginCol = beginCol;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Call{" + "beginLine=" + beginLine + ", beginCol=" + beginCol + ", endLine=" + endLine + ", endCol=" + endCol + ", path=" + path + '}';
    }


    
    
}
