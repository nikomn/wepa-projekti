/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("logged in as: " + username);
        model.addAttribute("username", username);
        return "start";
    }
    
    @GetMapping("/accountcreated")
    public String accountCreated(Model model) {
        return "accountcreated";
    }

    
}
