package com.immate.immate.service;

import com.immate.immate.dto.CrewInfo;
import com.immate.immate.dto.ProfitResponse;
import com.immate.immate.entity.user.*;
import com.immate.immate.repo.CrewRepository;
import com.immate.immate.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrewService {
    private final CrewRepository crewRepository;
    private final UserRepository userRepository;

    public CrewService(CrewRepository crewRepository, UserRepository userRepository) {
        this.crewRepository = crewRepository;
        this.userRepository = userRepository;
    }

    public List<CrewInfo> getMyCrews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        return crewRepository.findByUsers_Id(user.getId()).stream()
            .map(this::convertToCrewInfo)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProfitResponse getMyTotalProfit(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        List<Crew> userCrews = crewRepository.findByUsers_Id(user.getId());
        
        double totalProfit = userCrews.stream()
                .mapToDouble(Crew::getCrewYield)
                .average()
                .orElse(0.0);

        return ProfitResponse.builder()
                .totalProfit(totalProfit)
                .build();
    }

    public List<CrewInfo> getRecommendedCrews(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 사용자와 같은 투자 성향의 크루 1개
        List<Crew> sameStyleCrews = crewRepository.findByInvestmentStyleOrderByCrewYieldDesc(user.getInvestmentStyle());
        if (sameStyleCrews.isEmpty()) {
            throw new RuntimeException("투자 성향과 같은 크루를 찾을 수 없습니다");
        }
        
        // 다른 투자 성향의 크루 1개 추천 (예: 빅테크 선호, 배당주 선호, 국내장 선호, 미국장 선호 등)
        List<Crew> differentStyleCrews = crewRepository.findByInvestmentStyleOrderByCrewYieldDesc(user.getInvestmentStyle())
                .stream()
                .filter(crew -> !crew.getInvestmentStyle().equals(user.getInvestmentStyle()))
                .toList();
                
        if (differentStyleCrews.isEmpty()) {
            throw new RuntimeException("다른 투자 성향의 크루를 찾을 수 없습니다");
        }

        return List.of(
            convertToCrewInfo(sameStyleCrews.get(0)),
            convertToCrewInfo(differentStyleCrews.get(0))
        );
    }

    public List<CrewInfo> getTopRankingCrews() {
        return crewRepository.findAllByOrderByCrewYieldDesc().stream()
            .limit(3)
            .map(this::convertToCrewInfo)
            .collect(Collectors.toList());
    }

    private CrewInfo convertToCrewInfo(Crew crew) {
        return new CrewInfo(
            crew.getCrewName(),
            crew.getInvestmentStyle(),
            crew.getCrewYield()
        );
    }
} 