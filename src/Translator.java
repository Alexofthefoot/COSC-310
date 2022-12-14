import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Translator {

//    public static void main(String[] args) throws IOException {
//        String text = "Hello world!";
//        //Translated text: Hallo Welt!
//        System.out.println("Translated text: " + translate("en", "de", text));
//    }
    public static void translatedPrint(String text, String lang) throws IOException {
        System.out.println(translate("en", lang, text));
    }

    public static String translate(String langFrom, String langTo, String text) throws IOException {
        String urlStr = "https://script.google.com/macros/s/AKfycbxxRGfM5n1KoXT-d-jI8GELVdw_AN-ucMww2oxdomue9dCR-VOmlnYRKpFCIjTZZ6ABPw/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL myurl = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) myurl.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}