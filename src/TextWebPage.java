
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * Полдкльчение и получение одной веб страниы
 */

public class TextWebPage {
    private String theWebPageText = null;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private URLConnection getConnect(String url) {
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");
            return urlConnection;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getWebPage(String url) throws IOException {

        //подключение
        URLConnection urlConnection = getConnect(url);

        try {

            if(urlConnection != null && urlConnection.getContentType() != null && urlConnection.getContentType().matches(".*text/html.*")){

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
                    final StringBuilder stringBuilder = new StringBuilder();

                    String nextLine;
                    while ((nextLine = reader.readLine()) != null) {
                        stringBuilder.append(nextLine);
                        stringBuilder.append(LINE_SEPARATOR);
                    }
                    theWebPageText = stringBuilder.toString();
            }
        }catch (NullPointerException | FileNotFoundException e){
            e.printStackTrace();
        }
        return theWebPageText;
    }


    public String getTheWebPageText() {
        return theWebPageText;
    }

    public static String getLineSeparator() {
        return LINE_SEPARATOR;
    }
}
