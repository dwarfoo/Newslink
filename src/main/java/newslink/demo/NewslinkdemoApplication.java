package newslink.demo;

import antlr.collections.List;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import newslink.demo.Entities.NewsItem;
import newslink.demo.Entities.Person;
import newslink.demo.Entities.RssFeedParser;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;


@SpringBootApplication
@EnableCaching
@EnableScheduling
public class NewslinkdemoApplication {

    public static void main(String[] args) throws IOException, FeedException {
        SpringApplication.run(NewslinkdemoApplication.class, args);


    }

}
