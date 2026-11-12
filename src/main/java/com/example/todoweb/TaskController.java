package com.example.todoweb;

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

    // Show all tasks
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("tasks", repo.findAll());
        return "index";
    }

    // Add a new task
    @PostMapping("/add")
    public String add(@RequestParam String title) {
        Task task = new Task();
        task.setTitle(title);
        repo.save(task);
        return "redirect:/";
    }

    // Toggle done / not done
    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable String id) {
        Task task = repo.findById(id).orElseThrow();
        task.setCompleted(!task.isCompleted());
        repo.save(task);
        return "redirect:/";
    }

    // Delete a task
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}