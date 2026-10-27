package com.smartcafe.backend.service;

import com.smartcafe.backend.entity.Feedback;
import com.smartcafe.backend.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Feedback submitFeedback(Feedback feedback) {
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAllByOrderByCreatedAtDesc();
    }

    public Map<String, Object> getFeedbackSummary() {
        Double avgRating = feedbackRepository.getAverageRating();
        long totalReviews = feedbackRepository.count();
        List<Map<String, Object>> distribution = feedbackRepository.getRatingDistribution();

        Map<String, Object> summary = new HashMap<>();
        summary.put("averageRating", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        summary.put("totalReviews", totalReviews);
        summary.put("ratingDistribution", distribution);

        return summary;
    }
}
