/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    AccountRepository accountRepository;
    
    @Autowired
    ConnectionRepository connectionRepository;

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
        Account a = accountRepository.findByUsername(username);
        System.out.println("a.username = " + a.getUsername());
//        List<Connection> connections = accountRepository.findByUsername(username).getConnections();
        List<Connection> connections = new ArrayList<>();
        accountRepository.findByUsername(username).getConnections().forEach(connections::add);
//        
        List<Connection> connectedConections = new ArrayList<>();
        List<Connection> askedConections = new ArrayList<>();
        List<Connection> receivedConections = new ArrayList<>();
        System.out.println("Connections by user " + username);
        connections.forEach((c) -> {
            Connection newConnection = c;
            System.out.println("username: " + newConnection.getUsername() 
                    + ", accepted: " + newConnection.isAccepted() 
                    + ", asked: " + newConnection.isAsked() 
                    + ", rejected: " + newConnection.isRejected());
            if (newConnection.isAccepted()) {
                connectedConections.add(newConnection);
            } else if (!newConnection.isAccepted() && newConnection.isAsked() && !newConnection.isRejected()) {
                askedConections.add(newConnection);
            } else if (!newConnection.isAccepted() && !newConnection.isAsked()) {
                System.out.println("Menee ihan oikeaan paikkaan...");
                receivedConections.add(newConnection);
            }
            
        });
        
        /*
        for (Connection c: connections) {
            System.out.println(c);
        }*/
        model.addAttribute("connections", connectedConections);
        model.addAttribute("askedConnections", askedConections);
        model.addAttribute("receivedConnections", receivedConections);
        return "start";
    }
    
    @GetMapping("/accountcreated")
    public String accountCreated(Model model) {
        return "accountcreated";
    }

    
}
