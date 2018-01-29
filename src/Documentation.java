import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.HashMap;

public class Documentation {

    private ArrayList<Tag> params;
    private ArrayList<Tag> throwing;
    private Tag returns;

    private String description;
    private ArrayList<Tag> inlineTags;
    private ArrayList<Tag> tags;

    public Documentation() {
        this.params = new ArrayList<>();
        this.throwing = new ArrayList<>();

        this.inlineTags = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public ArrayList<Tag> getParams() {
        return params;
    }

    public void addParam(Tag newTag){
        this.params.add(newTag);
    }

    public ArrayList<Tag> getThrows() {
        return throwing;
    }

    public void addThrows(Tag newTag){
        this.throwing.add(newTag);
    }

    public Tag getReturns() {
        return returns;
    }

    public void setReturns(Tag newTag) {
        this.returns = newTag;
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
