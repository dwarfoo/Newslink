package newslink.demo.Services;

import java.util.List;
import newslink.demo.Entities.NewsItem;
import newslink.demo.Repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    public NewsService() {
    }

    public void saveNews(NewsItem newsItem){
        newsRepository.save(newsItem);
    }

    public NewsItem findById(long id){
        return newsRepository.findById(id);
    }

    public List<NewsItem> listAllByKeyword(String keyword, int startingArticle, int endingArticle, List<Long> alreadychecked) {

        if(keyword != null && keyword.length() == 0){
            keyword = null;
        }

        if (keyword != null) {

            return newsRepository.findAllByKeyword(keyword, startingArticle, endingArticle, alreadychecked);
        }
        return newsRepository.findAllByOrderByPubDateDesc(startingArticle, endingArticle, alreadychecked);
    }


    public NewsItem findByLink(String link){

        System.out.println("im finding links");
        return newsRepository.findByLink(link);
    }
    public List<NewsItem> findAllByPage(int startingArticle, int endingArticle,  List<Long> alreadychecked){
        return newsRepository.findAllByOrderByPubDateDesc(startingArticle, endingArticle, alreadychecked);
    }
    public List<NewsItem> findAll(){
        return newsRepository.findAll();
    }

}
