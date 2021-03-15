package newslink.demo.Services;

import newslink.demo.Entities.Person;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;



@Service
public class PersonService {

    public Person getPerson(Person person){
        System.out.println("Is this now cached" + person.getGender());
        return person;
    }
}
