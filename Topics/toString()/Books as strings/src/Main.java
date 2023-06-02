import java.util.Arrays;

class Book {

    private String title;
    private int yearOfPublishing;
    private String[] authors;

    public Book(String title, int yearOfPublishing, String[] authors) {
        this.title = title;
        this.yearOfPublishing = yearOfPublishing;
        this.authors = authors;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("title=").append(title).append(",");
        stringBuilder.append("yearOfPublishing=").append(yearOfPublishing).append(",");
        stringBuilder.append("authors=[");

        for (int i = 0; i < authors.length; i++) {
            stringBuilder.append(authors[i]);
            if (i != authors.length - 1) {
                stringBuilder.append(",");
            }
        }

        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}