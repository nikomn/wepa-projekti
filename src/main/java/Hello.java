import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Controller
@SpringBootApplication
public class Hello {

    @RequestMapping("/")
    @ResponseBody
    String home() {
      return "WEB-palvelinohjelmointi kurssi projekti!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Hello.class, args);
    }
}
