package PriceChecker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class PriceChecker {
    private URL url;

    public PriceChecker(String region, String realm, int itemId) {
        try {
            this.url = new URL("https://www.theunderminejournal.com/#" + region + "/" + realm + "/item/" + itemId);
        }
        catch(MalformedURLException e) {
            System.out.println("Error on URL creation.");
        }

    }

    public long getPrice() {
        long price = 0;
        System.out.println(this.url);
        Scanner sc;
        try {
          sc = new Scanner(this.url.openStream(), "UTF-8").useDelimiter("\\A");
          String page = sc.hasNext() ? sc.next() : "";

          if(!page.equals("")) {
              System.out.println(page);
              Document doc = Jsoup.parse(page);
              Element currentPrice = doc.select("tr.current-price > td > span").first();

              System.out.println(currentPrice.ownText());
          }
          else {
              System.out.println("Page empty!");
          }
        }
        catch(IOException e) {
            System.out.println("Error on loading Page.");
        }

        return price;
    }
}
