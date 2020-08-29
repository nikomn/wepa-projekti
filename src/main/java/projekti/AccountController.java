/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    SkillRepository skillRepository;
    

    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/accounts")
    public String list(Model model) {
        model.addAttribute("accounts", accountRepository.findAll());
        return "accounts";
    }

    @GetMapping("/search")
    public String searchConnections(Model model, @RequestParam String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        //System.out.println("auth detailssit: " + auth.getDetails());
        
        System.out.println("haetaan: " + name + "...");
        List<Account> accounts = accountRepository.findAll();
        List<Connection> connections = new ArrayList<>();
        accountRepository.findByUsername(username).getConnections().forEach(connections::add);
        List<Account> foundNewAccounts = new ArrayList<>();
        List<Account> foundConnectedAccounts = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getUsername().contains(name) && !a.getUsername().equals(username)) {
                System.out.println("Found by id: " + a.getId());
                //Account foundAcount = a;
                foundNewAccounts.add(a);
            } else {
                System.out.println(a.getUsername() + " != " + name);
            }
        }
        
        // Ugh... todo... fixaa myöhemmin, jos ehtii...
        List<Account> oldAccounts = new ArrayList<>();
        List<Account> newAccounts = new ArrayList<>();
        for (Account a : foundNewAccounts) {
            boolean isNew = true;
            for (Connection c : connections) {
                String u = c.getUsername();
                if (a.getUsername().equals(u)) {
                    isNew = false;
                } else {
                }
            }
            if (isNew) {
                newAccounts.add(a);
            } else {
                oldAccounts.add(a);
            }
        }

        model.addAttribute("found", newAccounts);
        model.addAttribute("foundconnected", oldAccounts);

        
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

        Account a = new Account(username, passwordEncoder.encode(password)
                , new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), new ArrayList<>());
        //Account a = new Account(username, passwordEncoder.encode(password), new ArrayList<>(), new FileObject());
        accountRepository.save(a);
        return "redirect:/accountcreated";
    }

    @Transactional
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

        return "redirect:/start";
    }

    @Transactional
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
    
    @Transactional
    @PostMapping("/accounts/{account}/skills")
    public String addSkill(@RequestParam String skill, @PathVariable(value = "account") String account) {
        Account a = accountRepository.findByUsername(account);
        Skill s = new Skill(skill, 0, a);
        skillRepository.save(s);
        
        return "redirect:/start";
    }
    
    @Transactional
    @PostMapping("/accounts/{account}/skills/{id}")
    public String likeSkill(@PathVariable(value = "account") String account, @PathVariable(value = "id") String skillId) {
        Account a = accountRepository.findByUsername(account);
        //System.out.println("skillID: " + skillId);
        Long id = Long.parseLong(skillId);
        //System.out.println("skillID: " + id);
        Skill likedSkill = skillRepository.getOne(id);
        //Skill likedSkillb = skillRepository.getOne(id);
        System.out.println("likedSkill: " + likedSkill.getName());
        //System.out.println("likedSkill: " + likedSkillb.getName());
//        List<Skill> allSkills = new ArrayList<>();
//        accountRepository.findByUsername(account).getSkills().forEach(allSkills::add);
        //Skill likedSkill = skillRepository.findByNameAndPosessor(skill, a);
        //Optional<Skill> findById = skillRepository.findById(id);
        //System.out.println("findById: " + findById.get());
//        for (int i = 0; i < allSkills.size(); i++) {
//            Skill s = allSkills.get(i);
//            if (s.getName().equals(skill)) {
//                likedSkill = s;
//                break;
//            }
//        }
        
        likedSkill.setLikes(likedSkill.getLikes() + 1);
        skillRepository.save(likedSkill);
        
        return "redirect:/users/{account}";
    }
    
    @Transactional
    @DeleteMapping("/accounts/{account}/{connection}")
    public String removeConnection(Model model, @PathVariable(value = "connection") String connection, @PathVariable(value = "account") String account) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);

        Account a = accountRepository.findByUsername(account);
        Account b = accountRepository.findByUsername(connection);

        List<Connection> connectionsOfA = new ArrayList<>();
        accountRepository.findByUsername(a.getUsername()).getConnections().forEach(connectionsOfA::add);
        List<Connection> connectionsOfB = new ArrayList<>();
        accountRepository.findByUsername(b.getUsername()).getConnections().forEach(connectionsOfB::add);
        for (Connection e : connectionsOfA) {
            if (e.getUsername().equals(b.getUsername())) {
                connectionRepository.delete(e);
                break;
            }
        }

        for (Connection e : connectionsOfB) {
            if (e.getUsername().equals(a.getUsername())) {
                connectionRepository.delete(e);
                break;
            }
        }

        return "redirect:/start";

    }
}
