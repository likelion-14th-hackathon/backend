package com.example.likelion14th_hackathon.recommendation.rule;

import com.example.likelion14th_hackathon.schedule.entity.ScheduleCategory;
import com.example.likelion14th_hackathon.style.entity.StyleType;

import java.util.List;
import java.util.Map;

import static com.example.likelion14th_hackathon.style.entity.StyleType.*;

public class TpoStyleRule {

    // 일정 카테고리 → 어울리는 스타일 후보
    private static final Map<ScheduleCategory, List<StyleType>> RULES = Map.of(
            ScheduleCategory.WEDDING,       List.of(CLASSIC, CHIC, ROMANTIC),
            ScheduleCategory.DATE,          List.of(ROMANTIC, CASUAL, CHIC),
            ScheduleCategory.INTERVIEW,     List.of(CLASSIC, MINIMAL),
            ScheduleCategory.WORK,          List.of(MINIMAL, CLASSIC, CHIC),
            ScheduleCategory.PARTY,         List.of(STREET, CHIC, VINTAGE),
            ScheduleCategory.TRAVEL,        List.of(CASUAL, SPORTY, STREET),
            ScheduleCategory.EXERCISE,      List.of(SPORTY),
            ScheduleCategory.CASUAL_MEETUP, List.of(CASUAL, STREET, MINIMAL),
            ScheduleCategory.FORMAL_EVENT,  List.of(CLASSIC, CHIC),
            ScheduleCategory.FUNERAL,       List.of(CLASSIC, MINIMAL)
    );

    // 일정에 맞는 TPO 추출
    // 규칙(일정->TPO)에 없는 카테고리면 빈 추천 반환
    public static List<StyleType> getStylesFor(ScheduleCategory category) {
        return RULES.getOrDefault(category, List.of());
    }

    private TpoStyleRule() {}   // 인스턴스화 방지
}