import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class Documentation {

    private ArrayList<String> params;
    private ArrayList<String> throwing;
    private boolean returns;

    public Documentation() {
        this.params = new ArrayList<>();
        this.throwing = new ArrayList<>();
        this.returns = false;
    }

    public Documentation(Documentation doc){
        this.params = doc.getParams();
        this.throwing = doc.getThrows();
        this.returns = doc.getReturns();
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public void addParam(String s){
        this.params.add(s);
    }

    public ArrayList<String> getThrows() {
        return throwing;
    }

    public void addThrows(String s){
        this.throwing.add(s);
    }

    public boolean getReturns() {
        return returns;
    }

    public void setReturns() {
        this.returns = true;
    }
}
