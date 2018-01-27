import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class Documentation {

    private ArrayList<String> params;
    private ArrayList<String> throwing;
    private boolean returns;

    private String description;
    private ArrayList<Tag> inlineTags;
    private ArrayList<Tag> tags;

    public Documentation() {
        this.params = new ArrayList<>();
        this.throwing = new ArrayList<>();
        this.returns = false;

        this.inlineTags = new ArrayList<>();
        this.tags = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Tag> getInlineTags() {
        return inlineTags;
    }

    public void addInlineTags(Tag inlineTag) {
        this.inlineTags.add(inlineTag);
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void addTags(Tag tag) {
        this.tags.add(tag);
    }
}
