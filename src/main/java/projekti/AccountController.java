/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @GetMapping("/newaccount")
    public String newaccount() {
        return "newuser";
    }

    @PostMapping("/newaccount")
    public String create(@RequestParam String username, @RequestParam String password) {
        //System.out.println("Tapahtuuko jotain?");
        //System.out.println("username: " + username);
        //System.out.println("password: " + username);
        Account a = new Account();
        a.setUsername(username);
        a.setPassword(passwordEncoder.encode(password));
        
        // delete from account where username = 'a';

        accountRepository.save(a);
        return "redirect:/";
    }


}
