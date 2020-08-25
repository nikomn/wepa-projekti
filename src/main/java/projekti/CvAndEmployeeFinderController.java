/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
@Controller
public class CvAndEmployeeFinderController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConnectionRepository connectionRepository;

    @Autowired
    private FileRepository fileRepository;

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
                //System.out.println("Menee ihan oikeaan paikkaan...");
                receivedConections.add(newConnection);
            }

        });

        
        model.addAttribute("connections", connectedConections);
        model.addAttribute("askedConnections", askedConections);
        model.addAttribute("receivedConnections", receivedConections);
        
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
