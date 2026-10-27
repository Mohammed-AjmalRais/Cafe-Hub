package com.smartcafe.backend.controller;

import com.smartcafe.backend.entity.Feedback;
import com.smartcafe.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    // Public API: Submit feedback
    @PostMapping("/feedback/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody Feedback feedback) {
        Feedback saved = feedbackService.submitFeedback(feedback);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Public/Admin API: Get all reviews
    @GetMapping("/feedback")
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    // Admin API: Get rating summary metrics
    @GetMapping("/admin/feedback/summary")
    public ResponseEntity<Map<String, Object>> getFeedbackSummary() {
        return ResponseEntity.ok(feedbackService.getFeedbackSummary());
    }
}
