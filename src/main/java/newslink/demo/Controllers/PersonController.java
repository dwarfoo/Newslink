package newslink.demo.Controllers;

import newslink.demo.Entities.NewsItem;
import newslink.demo.Services.NewsFetcher;
import newslink.demo.Entities.Person;
import newslink.demo.Services.NewsService;
import newslink.demo.Services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

@Controller
public class PersonController {
    @Autowired
    private PersonService personService;
    @Autowired
    private NewsService newsService;


    @GetMapping({"/me"})
    public String mePage(Model model) {

        model.addAttribute("person", new Person());

        return "PersonPage";
    }
    @GetMapping({"/copy"})
    public String copyToClip(Model model, HttpServletRequest request) {
        List<String> LinkList = new ArrayList<String>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            Arrays.stream(cookies)
                    .forEach(cookie -> {
                        if (cookie.getName().startsWith("ArticleList")){
                            List<String> stringList = Arrays.asList(cookie.getValue().split("-"));
                            List<Long> longList = new ArrayList<Long>();
                            stringList.forEach(string -> {
                               longList.add(Long.parseLong(string));
                            });

                            longList.forEach(idnumber -> {
                                LinkList.add(newsService.findById(idnumber).getLink());
                            });


                        }
                    });
        }
        String likedListString = "";
        for(int i = 0; i < LinkList.size(); i++){
            likedListString += LinkList.get(i) + "\r\n";
        }
        
        
        
        
        
        
        System.setProperty("java.awt.headless", "false");

        StringSelection stringSelection = new StringSelection(likedListString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);




        return "redirect:/";
    }

    @GetMapping("/location")
    public String location( HttpServletResponse response, @RequestParam(defaultValue = "0.0") final Double latitude
            , @RequestParam(defaultValue = "0.0") final Double longitude
            , final Model model)
    {

        String cords = "latitude="+latitude+"&long="+longitude;
        System.out.println(cords);

        if(latitude != null){

        Cookie cookie = new Cookie("cords", cords);
        cookie.setPath("/");
        System.out.println("its inside the building");
        cookie.setMaxAge(28 * 24 * 60 * 60);
        response.addCookie(cookie);
    }
        return "redirect:/";
    }

    @PostMapping({"/me"})
    public String confirmMePage(@ModelAttribute Person person, HttpServletResponse response) throws Exception {


        System.out.println(person.getGender().split(",")[0]);


        String stringAge = person.getAge().toString().replaceAll("/([A-Z][a-z]+)-?/g","");
        System.out.println(stringAge);
        String stringGender = person.getGender().split(",")[0];
        if(stringGender == null){
            stringGender = "Other";
        }
        Cookie cookie = new Cookie("age", stringAge);
        cookie.setPath("/");
        cookie.setMaxAge(28 * 24 * 60 * 60);
        response.addCookie(cookie);
        cookie = new Cookie("gender", stringGender);
        cookie.setPath("/");
        cookie.setMaxAge(28 * 24 * 60 * 60);
        response.addCookie(cookie);


        return "allowLocation";
    }

    @RequestMapping({"/"})
    public String mainPage(@ModelAttribute Person person, @Param("keyword") String keyword, Model model, HttpServletRequest request) throws IOException {


        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            List<String> idList = new ArrayList<String>();
            List<String> dislikedIdList = new ArrayList<String>();
            List<NewsItem> historyArticle = new ArrayList<NewsItem>();
            List<NewsItem> dislikedHistoryArticle = new ArrayList<NewsItem>();



            Arrays.stream(cookies)
                    .forEach(cookie -> {
                        System.out.println(cookie.getName());
                        if (cookie.getName().startsWith("ArticleList") && cookie.getMaxAge() !=0) {
                            idList.addAll(getListFromCookieString(cookie.getValue()));
                        } else if(cookie.getName().startsWith("DislikedArticleList")){
                            dislikedIdList.addAll(getListFromCookieString(cookie.getValue()));
                        }
                        else {
                            model.addAttribute(cookie.getName(), cookie.getValue());
                        }
                    });
            idList.forEach(id -> {

                    long longId = Long.valueOf(id);
                    if(newsService.findById(longId) != null) {
                        historyArticle.add(newsService.findById(longId));
                        System.out.println(newsService.findById(longId).getTitle() + " got added in historyArticle.");
                }

            });

            dislikedIdList.forEach(id -> {
                long longId = Long.valueOf(id);
                if(newsService.findById(longId) != null) {
                    dislikedHistoryArticle.add(newsService.findById(longId));
                }
            });

           Collections.reverse(Arrays.asList(historyArticle));

            model.addAttribute("checkedArticles", historyArticle.size() + dislikedHistoryArticle.size());
            model.addAttribute("world", getGenreAmountFromlists(historyArticle, dislikedHistoryArticle, "World"));
            model.addAttribute("feed", getGenreAmountFromlists(historyArticle, dislikedHistoryArticle, "Feed"));
            model.addAttribute("sport", getGenreAmountFromlists(historyArticle, dislikedHistoryArticle, "Sport"));
            model.addAttribute("kultur", getGenreAmountFromlists(historyArticle, dislikedHistoryArticle, "Kultur"));
            model.addAttribute("dislikedList", dislikedHistoryArticle);
            model.addAttribute("likedList", historyArticle);
            model.addAttribute("keyword", keyword);
            model.addAttribute("person", new Person());

            historyArticle.forEach(lo -> System.out.print(lo.getTitle()+ " ---- "));
            List<NewsItem> newHistory = historyArticle;

            newHistory.addAll(dislikedHistoryArticle);
            newHistory.stream().distinct();
            List<Long> combinedIdList = new ArrayList<>();
            newHistory.forEach(article ->{
                combinedIdList.add(article.getId());
            });

            System.out.println("does this happen");
            int page = 0;
            int amountOfArticles = 1;
            int startingArticle = (amountOfArticles * page);
            int endingArticle = amountOfArticles + (amountOfArticles * page);
            combinedIdList.add((long) 9999);
            List<NewsItem> listNewsItems = newsService.listAllByKeyword(keyword, startingArticle, endingArticle, combinedIdList);

            model.addAttribute("news", listNewsItems);
        } else {

            return "PersonPage";
//            return "MainPage";
        }

        return "MainPage";
    }




    @GetMapping({"like/{id}"})
    public String likeArticle(@Param("keyword") String keyword, @PathVariable long id, HttpServletResponse response, HttpServletRequest request) throws IOException {



        ArrayList<String> idList = new ArrayList<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null)


            Arrays.stream(cookies)
                    .forEach(cookie -> {
                        if(cookie.getName().startsWith("ArticleList")){
                            idList.addAll(getListFromCookieString(cookie.getValue()));
                            Cookie coo = new Cookie("ArticleList", null);
                            coo.setMaxAge(0);
                            coo.setPath("/");
                            response.addCookie(coo);

                        }
                    });




        Cookie cookie = new Cookie("ArticleList", geStringFromCookieList(idList) + id);
        cookie.setPath("/");
        cookie.setMaxAge(28 * 24 * 60 * 60);
        response.addCookie(cookie);
        System.out.println(cookie.getValue());

        if(keyword != null){
            return "redirect:/?keyword="+keyword;
        }


        return "redirect:/";
    }
    @GetMapping({"dislike/{id}"})
    public String dislikeArticle(@Param("keyword") String keyword,@PathVariable long id, HttpServletResponse response, HttpServletRequest request) throws IOException {



        ArrayList<String> idList = new ArrayList<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null)


            Arrays.stream(cookies)
                    .forEach(cookie -> {
                        if(cookie.getName().startsWith("DislikedArticleList")){
                            idList.addAll(getListFromCookieString(cookie.getValue()));
                            Cookie coo = new Cookie("DislikedArticleList", null);
                            coo.setMaxAge(0);
                            coo.setPath("/");
                            response.addCookie(coo);

                        }
                    });




        Cookie cookie = new Cookie("DislikedArticleList", geStringFromCookieList(idList) + id);
        cookie.setPath("/");
        cookie.setMaxAge(28 * 24 * 60 * 60);
        response.addCookie(cookie);
        System.out.println(cookie.getValue());


        if(keyword != null){
            return "redirect:/?keyword="+keyword;
        }

        return "redirect:/";
    }


    @GetMapping({"/{id}"})
    public String deleteArticle(@PathVariable long id, HttpServletResponse response, HttpServletRequest request) throws IOException {




        String stringId = Long.toString(id);
        ArrayList<String> idLists = new ArrayList<>();

        Cookie[] cookies = request.getCookies();
        if (cookies != null)


            Arrays.stream(cookies)
                    .forEach(cookie -> {
                        if(cookie.getName().startsWith("ArticleList")){
                            idLists.addAll(getListFromCookieString(cookie.getValue()));
                            Cookie coo = new Cookie("ArticleList", null);
                            coo.setMaxAge(0);
                            coo.setPath("/");
                            response.addCookie(coo);

                        }
                    });


        if(idLists.contains(stringId)){
            idLists.remove(stringId);
        }



        if(!geStringFromCookieList(idLists).isEmpty()) {
            Cookie cookie = new Cookie("ArticleList", geStringFromCookieList(idLists));
            cookie.setPath("/");
            cookie.setMaxAge(28 * 24 * 60 * 60);
            response.addCookie(cookie);
            System.out.println("value now:");
            System.out.println(cookie.getValue());

        }

        return "redirect:/";
    }


    public String geStringFromCookieList(List<String> list){
        String cookieString = "";
        for(int i = 0; i < list.size(); i ++) {

                        cookieString += list.get(i) + "-";
        }
        return cookieString;
    }

    public List getListFromCookieString(String value){


        return Arrays.asList(value.split("-").clone());

    }

    public double getGenreAmountFromlists(List<NewsItem> likedList, List<NewsItem> dislikedList, String genre){

        double sizeOfLikedItems = indexOfAll(likedList, genre);
        double sizeOfDislikedItems = indexOfAll(dislikedList, genre);
        double sizeOfBothLists = sizeOfLikedItems + sizeOfDislikedItems;
        System.out.println(sizeOfLikedItems + " / " + sizeOfBothLists);
        if(sizeOfBothLists != 0){

            double number = ((sizeOfLikedItems / sizeOfBothLists )* 100);
            System.out.println(number + "number returned ");
            return number;
        }else{

            return 0.0;
        }

    }

    public int indexOfAll(List<NewsItem> list, String genre) {
        final List<NewsItem> newsList = new ArrayList<>();
        System.out.println(list.size() + " : size of PARAMETER LIST");
        if(list.isEmpty()){
            return 0;
        }else{
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getGenre().equals(genre)) {
                    newsList.add(list.get(i));
                }
            }

            System.out.println(newsList.size() + " SIZE OF NEWSLIST");
            return newsList.size();
        }}


}