package com.example.todoweb;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TaskController {

    private final TaskRepository repo;

    public TaskController(TaskRepository repo) {
        this.repo = repo;
    }

    private String email(OAuth2User user) {
        return user.getAttribute("email");
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAttribute("tasks", repo.findByOwnerEmail(email(user)));
        model.addAttribute("userName", user.getAttribute("name"));
        model.addAttribute("userPicture", user.getAttribute("picture"));
        return "index";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title,
                      @AuthenticationPrincipal OAuth2User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setOwnerEmail(email(user));
        repo.save(task);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable String id,
                         @AuthenticationPrincipal OAuth2User user) {
        repo.findById(id)
            .filter(t -> email(user).equals(t.getOwnerEmail()))
            .ifPresent(t -> {
                t.setCompleted(!t.isCompleted());
                repo.save(t);
            });
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id,
                         @AuthenticationPrincipal OAuth2User user) {
        repo.findById(id)
            .filter(t -> email(user).equals(t.getOwnerEmail()))
            .ifPresent(t -> repo.delete(t));
        return "redirect:/";
    }

    @PostMapping("/delete-all")
    public String deleteAll(@AuthenticationPrincipal OAuth2User user) {
        repo.deleteAll(repo.findByOwnerEmail(email(user)));
        return "redirect:/";
    }

    @PostMapping("/delete-selected")
    public String deleteSelected(@RequestParam(required = false) List<String> ids,
                                 @AuthenticationPrincipal OAuth2User user) {
        if (ids != null && !ids.isEmpty()) {
            String email = email(user);
            List<Task> mine = repo.findAllById(ids).stream()
                    .filter(t -> email.equals(t.getOwnerEmail()))
                    .toList();
            repo.deleteAll(mine);
        }
        return "redirect:/";
    }
}
