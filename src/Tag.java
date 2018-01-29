public class Tag {
    private String tagType;
    private String text;
    private String ref;

    Tag(String tagType, String text, String ref){
        this.tagType = tagType.replaceAll("\\s", "").replace("*", "");
        this.text = text;
        this.ref = ref;
    }

    public String getTagType() {
        return tagType;
    }

    public String getText() {
        return text;
    }

    public String getRef() {
        return ref;
    }
}
