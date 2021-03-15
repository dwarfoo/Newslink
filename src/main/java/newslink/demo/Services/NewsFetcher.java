package newslink.demo.Services;

import com.rometools.rome.io.FeedException;
import newslink.demo.Entities.FuzzyScore;
import newslink.demo.Entities.NewsItem;
import newslink.demo.Entities.RssFeedParser;
import newslink.demo.Services.NewsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.getLevenshteinDistance;


@Component
public class NewsFetcher {

    @Autowired
    private NewsService newsService;

    public List<NewsItem> articleList;

    @Scheduled(fixedRate = 1200000)
    public void fetchData() {

        System.out.println("Checking for newly made articles... ");
        Map<String,String> urlList = new HashMap<String, String>();
        urlList.put("https://www.svt.se/kultur/rss.xml","Kultur");
        urlList.put("https://www.svt.se/sport/rss.xml","Sport");
        urlList.put("https://www.svt.se/nyheter/rss.xml","Feed");
        urlList.put("https://rss.aftonbladet.se/rss2/small/pages/sections/kultur/","Kultur");
        urlList.put("https://rss.aftonbladet.se/rss2/small/pages/sections/nyheter/","Feed");
        urlList.put("https://www.nytimes.com/svc/collections/v1/publish/https://www.nytimes.com/section/world/rss.xml","World");
        urlList.put("https://www.globalissues.org/news/feed","Feed");
        urlList.put("https://www.yahoo.com/news/rss/world","World");
        urlList.put("https://feeds.expressen.se/nyheter/","Feed");
        urlList.put("https://feeds.expressen.se/sport/","Sport");
        urlList.put("https://feeds.expressen.se/kultur/","Kultur");
        urlList.put("https://www.dn.se/rss/","Feed");
        urlList.put("https://mg.co.za/feed/","Feed");
        urlList.put("https://www.sowetanlive.co.za/rss/?publication=sowetan-live","Feed");

        articleList = newsService.findAll();
        List<String> linkList = new ArrayList<>();
        List<NewsItem> listOfTitles = new ArrayList<>();

        articleList.forEach(article -> {
            linkList.add(article.getLink());
        });


        for (Map.Entry<String, String> entry : urlList.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());


            RssFeedParser parser = new RssFeedParser();

            try {
                parser.ParseToNewsItem(entry).forEach(feed -> {

                    listOfTitles.add(feed);
                    if (!listContainsWord(linkList, feed.getLink())) {


                        System.out.println("Article found... Being added to DB");
                        newsService.saveNews(feed);
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (FeedException e) {
                e.printStackTrace();
            }



        };
        System.out.println(articleList.size());












        }



    public static boolean listContainsWord(List<String> list, String search) {
        return list != null && search != null && list.stream().anyMatch(search::equalsIgnoreCase);
    }

   // @Scheduled(fixedRate = 1200000)
    private void matchRecentNews() {
        List<NewsItem> listOfTitles = articleList;
        for (int i = 0; i < listOfTitles.size() - 1; i++) {
            for (int x = i; x < listOfTitles.size() - 1; x++) {
                String string1 = listOfTitles.get(i).getTitle();
                String string2 = listOfTitles.get(x).getTitle();
                if (!string1.equals(string2)) {
                    List<String> filteredString1 = getUpperCaseWords(string1);
                    List<String> filteredString2 = getUpperCaseWords(string2);


                    double sizeOfList = getBiggestList(filteredString1, filteredString2).size();
                    filteredString1.retainAll(filteredString2);


                    if (sizeOfList > 0 && (filteredString1.size() / sizeOfList) >= getPercentageBasedOnSize(sizeOfList)) {
                        ArrayList<NewsItem> newsItem = new ArrayList<NewsItem>();
                        System.out.println(filteredString1);
                        System.out.println(filteredString2);
                        System.out.println("It got added.");
                        NewsItem news1 = newsService.findByLink(listOfTitles.get(i).getLink());
                        NewsItem news2 = newsService.findByLink(listOfTitles.get(x).getLink());
                        //----------//
                        if(!news2.getSimilarNews().contains(news1) && !news1.getSimilarNews().contains(news2)){
                            newsItem.add(news1);
                            newsItem.addAll(news2.getSimilarNews());
                            news2.setSimilarNews(newsItem);
                            newsService.saveNews(news2);

                            System.out.println("alert BAD-----------------");
                            newsItem.clear();

                            newsItem.add(news2);
                            newsItem.addAll(news1.getSimilarNews());
                            news1.setSimilarNews(newsItem);
                            newsService.saveNews(news1);
                        }

                    }

                }


            }
            }


    }

    public double getPercentageBasedOnSize(double size) {

    if (size > 5) {
            return 0.5;
    }
            else{
            return 0.375;
            }

    }

    public List  getBiggestList(List list1, List list2){
        if(list1.size() > list2.size()){
            return list1;
        }else{
            return list2;
        }
    }
    public List<String> getUpperCaseWords(String title){
        List<String> upperCaseWords = new ArrayList<String>();

        List<String> words = Arrays.asList(title.split(" "));


       for(int i= 0; i < words.size(); i++){
           if(words.get(i).matches("(?U)\\b\\p{Lu}\\p{L}*\\b")){
               if(words.get(i).length() > 4) {
                   upperCaseWords.add(words.get(i));
               }
           }
       }

       return upperCaseWords;
    }
}
