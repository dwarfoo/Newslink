package newslink.demo.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NewsItem implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String link;
    @Lob
    @Column( length = 100000 )
    private String description;
    private Date pubDate;
    private String genre;
    private String language;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<NewsItem> similarNews;


    @Override
    public String toString() {
        return "Feed [description=" + description
                +", link=" + link + ", pubDate="
                + pubDate + ", title=" + title + "]";
    }


    public boolean doIExistInList(List<NewsItem> newsItems){
        if(newsItems.contains(this)){
            return true;
        }else{
            return false;
        }
    }


}
