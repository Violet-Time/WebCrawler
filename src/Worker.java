
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Worker implements Runnable {


    CreatorWorkers workers;


    private Matcher linksMatcher;

    private Matcher titleMatcher;

    private MyData data;

    Worker(CreatorWorkers workers){

        this.linksMatcher = Pattern.compile(
                CreatorWorkers.REQUEST_LINKS
        ).matcher("");
        this.titleMatcher = Pattern.compile(
                CreatorWorkers.REQUEST_TITLE
        ).matcher("");
        this.workers = workers;
    }

    public void setData(MyData data) {
        this.data = data;
    }

    void parse(String url, int depth) throws IOException {

        TextWebPage webPage  = new TextWebPage();

        if (webPage.getWebPage(url) != null) {

            linksMatcher.reset(webPage.getTheWebPageText());

            while (linksMatcher.find()) {

                if(workers.getStatus() == 0){
                    break;
                }

                String link = linksMatcher.group().replaceAll("href=[\"\']/?", "");

                if (!link.matches("^https?://.+")) {

                    if (link.matches(".+\\.(?!html|php).*(\\/.+\\.(html|php)$)?")) {

                        link = "https:" + (link.matches("^/.*") ? "" : "/") + (link.matches("^//.*") ? "" : "/") + link;

                    } else if (!link.matches(url.replaceAll("\\/[^\\/]+$", ""))) {

                        link = url.replaceAll("\\/[^\\/]+$", "") + (url.matches(".*/$") ? "" : "/") + link;

                    }
                }
                //System.out.println(depth + 1 + " - - " + link + " -- MaxDepth -- " + workers.getMaxDepth());
                if(depth + 1 <= workers.getMaxDepth()) {
                    workers.dataForCrawler.add(new MyData(link, depth + 1));
                }

            }
            if(depth > 0 ) {
                workers.saveUrlAndTitle.put(url, searchTitleLabel(webPage.getTheWebPageText()));
            }

        }

    }

    String searchTitleLabel(String webPage){
        titleMatcher.reset(webPage);

        if (titleMatcher.find()) {
            return titleMatcher.group().replaceAll("</?title>", "");
        } else {
            return "";
        }
    }

    @Override
    public void run(){
            while (workers.getStatus() == 1){
                if(data != null){
                    System.out.println(Thread.currentThread().getName());
                    if ((workers.getMaxDepth() == 0 || data.getDepth() <= workers.getMaxDepth()) && !workers.saveUrlAndTitle.containsKey(data.getUrl())) {
                        try {
                            parse(data.getUrl(), data.getDepth());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    data = null;
                    workers.looseWorkers.add(this);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
}
