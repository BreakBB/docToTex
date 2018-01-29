public class Tag {
    private String tagType;
    private String tagName;
    private String text;
    private String ref;

    Tag(String tagType, String tagName, String text, String ref){
        this.tagType = tagType.replaceAll("\\s", "").replace("*", "");
        this.tagName = tagName;
        this.text = text;
        this.ref = ref;
    }

    public String getTagType() {
        return tagType;
    }

    public String getTagName(){
        return tagName;
    }

    public String getText() {
        return text;
    }

    public String getRef() {
        return ref;
    }
}
