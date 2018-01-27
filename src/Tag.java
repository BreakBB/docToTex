public class Tag {
    private String tagType;
    private String text;

    Tag(String tagType, String text){
        this.tagType = tagType;
        this.text = text;
    }

    public String getTagType() {
        return tagType;
    }

    public String getText() {
        return text;
    }
}
