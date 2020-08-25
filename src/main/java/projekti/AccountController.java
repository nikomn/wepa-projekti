/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/accounts")
    public String list(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    @GetMapping("/search")
    public String searchConnections(Model model, @RequestParam String name) {
        System.out.println("haetaan: " + name + "...");
        List<Account> accounts = accountRepository.findAll();
        List<Account> foundAccounts = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getUsername().contains(name)) {
                System.out.println("Found by id: " + a.getId());
                foundAccounts.add(a);
            } else {
                System.out.println(a.getUsername() + " != " + name);
            }
        }

        model.addAttribute("results", foundAccounts);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        System.out.println("auth detailssit: " + auth.getDetails());
        //model.addAttribute("userid", auth.getDetails());
        return "results";
    }

    @GetMapping("/newaccount")
    public String create(Model model) {
        return "newaccount";
    }

    @PostMapping("/accounts")
    public String add(@RequestParam String username, @RequestParam String password) {
        if (accountRepository.findByUsername(username) != null) {
            return "redirect:/newaccount";
        }

        Account a = new Account(username, passwordEncoder.encode(password), new ArrayList<>(), new ArrayList<>());
        //Account a = new Account(username, passwordEncoder.encode(password), new ArrayList<>(), new FileObject());
        accountRepository.save(a);
        return "redirect:/accountcreated";
    }

    @PostMapping("/accounts/accept/{account}/{connection}")
    public String makeConnection(Model model, @PathVariable(value = "connection") String connection, @PathVariable(value = "account") String account) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);

        Account a = accountRepository.findByUsername(account);
        Account b = accountRepository.findByUsername(connection);

//        Connection c = new Connection(b.getUsername(), true, false, false, a);
//        Connection c2 = new Connection(a.getUsername(), false, false, false, b);
        List<Connection> connectionsOfA = new ArrayList<>();
        accountRepository.findByUsername(a.getUsername()).getConnections().forEach(connectionsOfA::add);
        List<Connection> connectionsOfB = new ArrayList<>();
        accountRepository.findByUsername(b.getUsername()).getConnections().forEach(connectionsOfB::add);
        for (Connection e : connectionsOfA) {
            if (e.getUsername().equals(b.getUsername())) {
                e.setAccepted(true);
                connectionRepository.save(e);
                break;
            }
        }
        
        for (Connection e : connectionsOfB) {
            if (e.getUsername().equals(a.getUsername())) {
                e.setAccepted(true);
                connectionRepository.save(e);
                break;
            }
        }

            

//        if (connectionsOfA.contains(c)) {
            return "redirect:/start";
    }

    @PostMapping("/accounts/{account}/{connection}")
    public String addConnection(Model model, @PathVariable(value = "connection") String connection, @PathVariable(value = "account") String account) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);

        Account a = accountRepository.findByUsername(account);
        Account b = accountRepository.findByUsername(connection);
        Connection c = new Connection(b.getUsername(), true, false, false, a);
        connectionRepository.save(c);
        Connection c2 = new Connection(a.getUsername(), false, false, false, b);
        connectionRepository.save(c2);
        List<Connection> connectionsOfA = new ArrayList<>();
        accountRepository.findByUsername(a.getUsername()).getConnections().forEach(connectionsOfA::add);
        List<Connection> connectionsOfB = new ArrayList<>();
        accountRepository.findByUsername(b.getUsername()).getConnections().forEach(connectionsOfB::add);

        return "redirect:/start";
    }
}
