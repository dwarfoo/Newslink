package newslink.demo.Repositories;

import newslink.demo.Entities.NewsItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<NewsItem, Long> {


    NewsItem findById(long id);

    NewsItem findByLink(String link);

    @Query(value = "SELECT * FROM newslink.news_item where id NOT IN ?3 order by pub_date desc limit ?1, ?2", nativeQuery = true)
    List<NewsItem> findAllByOrderByPubDateDesc(int startingArticle, int endingArticle,  List<Long> alreadyChecked);


    @Query(value = "SELECT * FROM newslink.news_item WHERE title LIKE %?1% OR link LIKE %?1% AND id NOT IN ?4 order by pub_date desc limit ?2, ?3" , nativeQuery = true)
    List<NewsItem> findAllByKeyword(String keyword, int startingArticle, int endingArticle, List<Long> alreadyChecked);
}
