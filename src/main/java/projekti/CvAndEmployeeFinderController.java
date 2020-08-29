/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootApplication
@Controller
public class CvAndEmployeeFinderController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    private FileRepository fileRepository;
    
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "info";
    }
    
    @GetMapping("/wall")
    public String wall(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("date").descending());
        model.addAttribute("username", username);
        
        
//        List<Connection> connections = new ArrayList<>();
//        accountRepository.findByUsername(username).getConnections().forEach(connections::add);
//        
//        List<Post> allPosts = new ArrayList<>();
//        postRepository.findAll().forEach(allPosts::add);
//        
//        List<Post> postsByConections = new ArrayList<>();
//        for (Account a : foundNewAccounts) {
//        }
//        List<Account> oldAccounts = new ArrayList<>();
//        List<Account> newAccounts = new ArrayList<>();
//        for (Account a : foundNewAccounts) {
//            boolean isNew = true;
//            for (Connection c : connections) {
//                String u = c.getUsername();
//                if (a.getUsername().equals(u)) {
//                    isNew = false;
//                } else {
//                }
//            }
//            if (isNew) {
//                newAccounts.add(a);
//            } else {
//                oldAccounts.add(a);
//            }
//        }
//        allPosts.forEach((p) -> {
//            Post newPost = p;
//            if (newConnection.getUsername().equals(username)) {
//                //System.out.println("If lause...");
//                model.addAttribute("connectionPendingOrDone", true);
//            }
//            System.out.println("username: " + newConnection.getUsername()
//                    + ", accepted: " + newConnection.isAccepted()
//                    + ", asked: " + newConnection.isAsked()
//                    + ", rejected: " + newConnection.isRejected());
//            if (newConnection.isAccepted()) {
//                connectedConections.add(newConnection);
//            } else if (!newConnection.isAccepted() && newConnection.isAsked() && !newConnection.isRejected()) {
//                askedConections.add(newConnection);
//            } else if (!newConnection.isAccepted() && !newConnection.isAsked()) {
//                receivedConections.add(newConnection);
//            }
//
//        });
        
        
        
        
        
        model.addAttribute("posts", postRepository.findAll(pageable));
        return "wall";
    }
    
    
    @GetMapping("/users/{account}")
    public String userpage(Model model, @PathVariable(value = "account") String account) {
        Account a = accountRepository.findByUsername(account);
        //boolean isConnected = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("logged in as: " + username);
        model.addAttribute("username", username);
        
        model.addAttribute("userpage", account);
        
        
        List<Connection> connections = new ArrayList<>();
        accountRepository.findByUsername(account).getConnections().forEach(connections::add);
        
        List<Connection> connectedConections = new ArrayList<>();
        List<Connection> askedConections = new ArrayList<>();
        List<Connection> receivedConections = new ArrayList<>();
        System.out.println("Connections by user " + account);
        connections.forEach((c) -> {
            Connection newConnection = c;
            if (newConnection.getUsername().equals(username)) {
                //System.out.println("If lause...");
                model.addAttribute("connectionPendingOrDone", true);
            }
            System.out.println("username: " + newConnection.getUsername()
                    + ", accepted: " + newConnection.isAccepted()
                    + ", asked: " + newConnection.isAsked()
                    + ", rejected: " + newConnection.isRejected());
            if (newConnection.isAccepted()) {
                connectedConections.add(newConnection);
            } else if (!newConnection.isAccepted() && newConnection.isAsked() && !newConnection.isRejected()) {
                askedConections.add(newConnection);
            } else if (!newConnection.isAccepted() && !newConnection.isAsked()) {
                receivedConections.add(newConnection);
            }

        });

        model.addAttribute("connections", connectedConections);
//        model.addAttribute("askedConnections", askedConections);
//        model.addAttribute("receivedConnections", receivedConections);

        List<Skill> allSkills = new ArrayList<>();
        accountRepository.findByUsername(account).getSkills().forEach(allSkills::add);

        
        allSkills.sort((o1, o2) -> o2.getLikes().compareTo(o1.getLikes()));
        
        List<Skill> topSkills = new ArrayList<>();
        List<Skill> otherSkills = new ArrayList<>();
        for (int i = 0; i < allSkills.size(); i++) {
            if (i < 3) {
                topSkills.add(allSkills.get(i));
            } else {
                otherSkills.add(allSkills.get(i));
            }
        }

        model.addAttribute("topSkills", topSkills);
        model.addAttribute("otherSkills", otherSkills);

        List<FileObject> fiilit = new ArrayList<>();
        accountRepository.findByUsername(account).getFiles().forEach(fiilit::add);
        fiilit.forEach((f) -> {
            FileObject newFile = f;
            System.out.println("filename: " + newFile.getName());
            System.out.println("id: " + newFile.getId());
        });
        if (fiilit.size() > 0) {
            model.addAttribute("picId", fiilit.get(0).getId());
        }
        
        return "userpage";
    }

    @Transactional
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
                //System.out.println("Menee ihan oikeaan paikkaan...");
                receivedConections.add(newConnection);
            }

        });

        model.addAttribute("connections", connectedConections);
        model.addAttribute("askedConnections", askedConections);
        model.addAttribute("receivedConnections", receivedConections);

        List<Skill> allSkills = new ArrayList<>();
        accountRepository.findByUsername(username).getSkills().forEach(allSkills::add);
 
//        List<Skill> allSkillsAsNormalList = new ArrayList<>();
//        allSkills.forEach((s) -> {
//            Skill x = s;
//            allSkillsAsNormalList.add(x);
//        });
        
        allSkills.sort((o1, o2) -> o2.getLikes().compareTo(o1.getLikes()));
        
        List<Skill> topSkills = new ArrayList<>();
        List<Skill> otherSkills = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < allSkills.size(); i++) {
            if (i < 3) {
                topSkills.add(allSkills.get(i));
            } else {
                otherSkills.add(allSkills.get(i));
            }
        }

        //model.addAttribute("skills", accountRepository.findByUsername(username).getSkills());
        model.addAttribute("topSkills", topSkills);
        model.addAttribute("otherSkills", otherSkills);

        List<FileObject> fiilit = new ArrayList<>();
        accountRepository.findByUsername(username).getFiles().forEach(fiilit::add);
        //fileRepository.findAll().forEach(fiilit::add);
        fiilit.forEach((f) -> {
            FileObject newFile = f;
            System.out.println("filename: " + newFile.getName());
            System.out.println("id: " + newFile.getId());
        });
        if (fiilit.size() > 0) {
            model.addAttribute("picId", fiilit.get(0).getId());
        }

        return "start";
    }

    @GetMapping("/accountcreated")

    public String accountCreated(Model model) {
        return "accountcreated";
    }

    @GetMapping(value = "{account}/files/{id}")
    public String viewOne(Model model, @PathVariable Long id) {
        model.addAttribute("pic", id);
        return "start";
    }

    @GetMapping(path = "/{account}/files/{id}/content")
    @ResponseBody
    public byte[] getContent(@PathVariable Long id) {
        return fileRepository.getOne(id).getContent();
    }

    @PostMapping("/start/{account}/files")
    public String addFile(@RequestParam("file") MultipartFile file, @PathVariable(value = "account") String account) throws IOException {
        Account a = accountRepository.findByUsername(account);
        System.out.println("fiilin content type: " + file.getContentType());
        if (file.getContentType().equals("image/gif") || file.getContentType().equals("image/png")) {
            Account owner = accountRepository.findByUsername(account);
            FileObject fileObject = new FileObject();
            fileObject.setContentType(file.getContentType());
            fileObject.setContent(file.getBytes());
            fileObject.setName(file.getOriginalFilename());
            fileObject.setContentLength(file.getSize());
            fileObject.setOwner(owner);
            List<FileObject> fiilit = new ArrayList<>();
            accountRepository.findByUsername(account).getFiles().forEach(fiilit::add);
            if (fiilit.size() > 0) {
                fileRepository.delete(fiilit.get(0));
            }
            fileRepository.save(fileObject);
        } else {
            System.out.println("Only gif and png allowed at this point...");
        }

        return "redirect:/start";
    }

}
