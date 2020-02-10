
/**
 * Класс для хранения одной ссылки и ее глубины
 */

public class MyData {
    //глубина
    private int depth;
    //ссылка
    private String url;



    MyData(String url, int depth){
        this.depth = depth;
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public String getUrl() {
        return url;
    }
}
