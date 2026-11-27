package com.example.todoweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private static final int MAX_TASKS = 30;

    private final TaskRepository taskRepository;
    private final AiService aiService;

    public AiController(TaskRepository taskRepository, AiService aiService) {
        this.taskRepository = taskRepository;
        this.aiService = aiService;
    }

    @PostMapping("/prioritize")
    public ResponseEntity<?> prioritize(@AuthenticationPrincipal OAuth2User user) {
        String email = user.getAttribute("email");

        // Only THIS user's pending tasks (the old findAll() leaked everyone's tasks)
        List<Task> pending = taskRepository.findByOwnerEmail(email).stream()
                .filter(t -> !t.isCompleted())
                .limit(MAX_TASKS)
                .toList();

        if (pending.size() < 2) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Add at least 2 pending tasks first."));
        }

        try {
            List<Task> ordered = aiService.prioritize(pending);
            return ResponseEntity.ok(ordered.stream()
                    .map(t -> Map.of("id", t.getId(), "title", t.getTitle()))
                    .toList());
        } catch (Exception e) {
            log.error("AI prioritize failed", e);   // full error in server logs only
            return ResponseEntity.status(502)
                    .body(Map.of("error", "AI is unavailable right now. Try again."));
        }
    }
}
