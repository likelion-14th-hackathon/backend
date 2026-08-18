package com.example.likelion14th_hackathon.recommendation.service;

import com.example.likelion14th_hackathon.catalog.domain.Product;
import com.example.likelion14th_hackathon.member.domain.Member;
import com.example.likelion14th_hackathon.member.repository.MemberRepository;
import com.example.likelion14th_hackathon.mypage.domain.OwnedProduct;
import com.example.likelion14th_hackathon.mypage.domain.OwnershipStatus;
import com.example.likelion14th_hackathon.mypage.repository.OwnedProductRepository;
import com.example.likelion14th_hackathon.recommendation.dto.RecommendationResponse;
import com.example.likelion14th_hackathon.recommendation.dto.RecommendationResponse.ProductSummary;
import com.example.likelion14th_hackathon.recommendation.rule.TpoStyleRule;
import com.example.likelion14th_hackathon.schedule.entity.Schedule;
import com.example.likelion14th_hackathon.schedule.repository.ScheduleRepository;
import com.example.likelion14th_hackathon.style.entity.ProductStyle;
import com.example.likelion14th_hackathon.style.entity.StyleType;
import com.example.likelion14th_hackathon.style.entity.UserStyle;
import com.example.likelion14th_hackathon.style.repository.ProductStyleRepository;
import com.example.likelion14th_hackathon.style.repository.UserStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ScheduleRepository scheduleRepository;
    private final ProductStyleRepository productStyleRepository;
    private final UserStyleRepository userStyleRepository;
    private final OwnedProductRepository ownedProductRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public RecommendationResponse recommendForSchedule(Long scheduleId, Long memberId) {

        // 0. 일정 조회 + 소유자 검증
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!schedule.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("해당 일정에 대한 권한이 없습니다.");
        }

        // 1. TPO 규칙: 일정 카테고리 → 어울리는 스타일 후보
        List<StyleType> tpoStyles = TpoStyleRule.getStylesFor(schedule.getCategory());
        if (tpoStyles.isEmpty()) {
            return new RecommendationResponse(scheduleId, schedule.getCategory(),
                    schedule.getTitle(), List.of(), List.of());
        }

        // 2. 그 스타일을 가진 제품 조회 (제품 단위로 중복 제거)
        List<ProductStyle> matched = productStyleRepository.findByStyleTypeIn(tpoStyles);
        Map<Long, Product> candidateMap = new LinkedHashMap<>();
        for (ProductStyle ps : matched) {
            Product p = ps.getProduct();
            candidateMap.putIfAbsent(p.getProductId(), p);
        }
        List<Product> candidates = new ArrayList<>(candidateMap.values());

        // 3. 회원 취향 수집
        //  (1) AI가 사진으로 분석한 스타일
        Set<StyleType> memberStyles = userStyleRepository.findByMember(member).stream()
                .map(UserStyle::getStyleType)
                .collect(Collectors.toSet());

        //  (2) 구매 이력 전체 (양도한 것도 취향 신호로 포함)
        List<OwnedProduct> allPurchased = ownedProductRepository.findByMember_Id(memberId);

        Map<StyleType, Long> purchasedStyleCount = new HashMap<>();
        for (OwnedProduct op : allPurchased) {
            List<ProductStyle> styles = productStyleRepository.findByProduct(op.getProduct());
            for (ProductStyle ps : styles) {
                purchasedStyleCount.merge(ps.getStyleType(), 1L, Long::sum);
            }
        }

        // 4. 정렬: (AI 스타일 겹침) + (구매 스타일 횟수) 높은 순
        Map<Long, Set<StyleType>> productStyleMap = buildProductStyleMap(matched);
        candidates.sort((a, b) -> {
            long scoreA = score(productStyleMap.get(a.getProductId()), memberStyles, purchasedStyleCount);
            long scoreB = score(productStyleMap.get(b.getProductId()), memberStyles, purchasedStyleCount);
            return Long.compare(scoreB, scoreA);
        });

        // 5. 보유/미보유 분리 (각각 최상위 1개만 추천)
        Set<Long> ownedProductIds = allPurchased.stream()
                .filter(op -> op.getStatus() == OwnershipStatus.OWNED)
                .map(op -> op.getProduct().getProductId())
                .collect(Collectors.toSet());

        List<ProductSummary> owned = new ArrayList<>();
        List<ProductSummary> notOwned = new ArrayList<>();

        for (Product p : candidates) {
            // 양쪽 다 채워졌으면 더 볼 필요 없음
            if (!owned.isEmpty() && !notOwned.isEmpty()) {
                break;
            }

            ProductSummary summary = new ProductSummary(
                    p.getProductId(), p.getName(), p.getImageUrl());

            if (ownedProductIds.contains(p.getProductId())) {
                if (owned.isEmpty()) {
                    owned.add(summary);
                }
            } else {
                if (notOwned.isEmpty()) {
                    notOwned.add(summary);
                }
            }
        }
        return new RecommendationResponse(scheduleId, schedule.getCategory(),
                schedule.getTitle(), owned, notOwned);
    }

    /** 제품별 스타일 집합 구성 (점수 계산용) */
    private Map<Long, Set<StyleType>> buildProductStyleMap(List<ProductStyle> matched) {
        Map<Long, Set<StyleType>> map = new HashMap<>();
        for (ProductStyle ps : matched) {
            Long pid = ps.getProduct().getProductId();
            map.computeIfAbsent(pid, k -> new HashSet<>()).add(ps.getStyleType());
        }
        return map;
    }

    /** 추천 점수 = (AI 분석 스타일과 겹침 수) + (구매 이력의 해당 스타일 횟수 합) */
    private long score(Set<StyleType> productStyles,
                       Set<StyleType> memberStyles,
                       Map<StyleType, Long> purchasedStyleCount) {
        if (productStyles == null) return 0;

        long aiScore = productStyles.stream()
                .filter(memberStyles::contains)
                .count();

        long purchaseScore = productStyles.stream()
                .mapToLong(s -> purchasedStyleCount.getOrDefault(s, 0L))
                .sum();

        return aiScore + purchaseScore;
    }
}