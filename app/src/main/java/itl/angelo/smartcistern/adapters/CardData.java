package itl.angelo.smartcistern.adapters;

public class CardData {
    private String title;
    private String subtitle1;
    private String subtitle2;
    private String content1;
    private String content2;

    public CardData(String title, String subtitle1, String content1,String subtitle2, String content2) {
        this.title = title;
        this.subtitle1 = subtitle1;
        this.content1 = content1;
        this.subtitle2 = subtitle2;
        this.content2 = content2;
    }

    public String getTitle() {
        return title;
    }

    public String getContent1() {
        return content1;
    }

    public String getContent2() {
        return content2;
    }

    public String getSubtitle1() {
        return subtitle1;
    }

    public String getSubtitle2() {
        return subtitle2;
    }
}