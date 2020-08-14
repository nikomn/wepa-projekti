/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class CvAndEmployeeFinderController {

    @GetMapping("/")
    public String home(Model model) {
        return "info";
    }
    
    @GetMapping("/start")
    public String start(Model model) {
        return "start";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CvAndEmployeeFinderController.class, args);
    }
}