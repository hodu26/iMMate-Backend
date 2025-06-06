package com.immate.immate.controller;

import com.immate.immate.dto.CrewInfo;
import com.immate.immate.dto.ProfitResponse;
import com.immate.immate.service.CrewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crews")
public class CrewController {
    private final CrewService crewService;

    public CrewController(CrewService crewService) {
        this.crewService = crewService;
    }

    @GetMapping("/my-crews")
    public ResponseEntity<List<CrewInfo>> getMyCrews(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(crewService.getMyCrews(userEmail));
    }

    @GetMapping("/my-profit")
    public ResponseEntity<ProfitResponse> getMyTotalProfit(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(crewService.getMyTotalProfit(userEmail));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<CrewInfo>> getRecommendedCrews(@AuthenticationPrincipal UserDetails userDetails) {
        String userEmail = userDetails.getUsername();
        return ResponseEntity.ok(crewService.getRecommendedCrews(userEmail));
    }

    @GetMapping("/top-ranking")
    public ResponseEntity<List<CrewInfo>> getTopRankingCrews() {
        return ResponseEntity.ok(crewService.getTopRankingCrews());
    }
} 