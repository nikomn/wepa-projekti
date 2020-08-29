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
import java.time.LocalDateTime;

@Controller
public class PostController {
    
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ConnectionRepository connectionRepository;
    
    @Autowired
    PostRepository postRepository;
    
    @Autowired
    CommentRepository commentRepository;
    
    @Transactional
    @PostMapping("/accounts/{account}/posts")
    public String addPost(@RequestParam String posting, @PathVariable(value = "account") String account) {
        Account a = accountRepository.findByUsername(account);
        Post p = new Post(posting, 0, LocalDateTime.now(), a, new ArrayList<>());
        postRepository.save(p);
        
        return "redirect:/wall";
    }
    
    
    @Transactional
    @PostMapping("/posts/{id}/comments")
    public String addComment(@RequestParam String comment, @PathVariable Long id) {
        
        Post p = postRepository.getOne(id);
        Comment c = new Comment(comment, p);
        commentRepository.save(c);
        
        return "redirect:/wall";
    }
    
    @Transactional
    @PostMapping("/posts/{id}/like")
    public String likeSkill(@PathVariable Long id) {
        Post p = postRepository.getOne(id);
        p.setLikes(p.getLikes() + 1);
        
        postRepository.save(p);
        
        return "redirect:/wall";
    }
    
}
