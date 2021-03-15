package newslink.demo.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import newslink.demo.Services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person implements Serializable{



    private String gender;
    private String country;
    private String age;
    private String politicalStance;




}
