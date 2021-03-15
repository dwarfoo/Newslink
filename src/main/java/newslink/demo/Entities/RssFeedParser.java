package newslink.demo.Entities;


import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import newslink.demo.Controllers.EnglishKeyWords;


import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RssFeedParser {


    public ArrayList<NewsItem> ParseToNewsItem(Map.Entry<String,String> urlAdress) throws IOException, FeedException, FeedException {



        ArrayList<NewsItem> newsList = new ArrayList<>();
        URL rssUrl = new URL(urlAdress.getKey());
        BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));

        SyndFeed feed = new SyndFeedInput().build(in);
        feed.getEntries().forEach(fee -> {
            NewsItem item = new NewsItem();

            if(fee.getDescription() != null) {
                List<EnglishKeyWords> enumValues = Arrays.asList(EnglishKeyWords.values());
                int amountOfEnglishKeywordsInString = 0;
                for (int i = 0; i < enumValues.size(); i++) {
                    if (fee.getDescription().toString().contains(" " + enumValues.get(i).toString() + " ")
                            || fee.getTitle().contains(" " + enumValues.get(i).toString() + " ")) {
                        amountOfEnglishKeywordsInString = amountOfEnglishKeywordsInString + 1;
                    }
                }
                if (amountOfEnglishKeywordsInString > 2 || fee.getLink().contains(".com")) {
                    item.setLanguage("English");
                } else {
                    item.setLanguage("Swedish");
                }


                item.setDescription(fee.getDescription().toString());



            item.setGenre(urlAdress.getValue());
            item.setTitle(fee.getTitle());
            item.setPubDate(fee.getPublishedDate());
            item.setLink(fee.getLink());

            newsList.add(item);
            }

        });



return newsList;
    }
}