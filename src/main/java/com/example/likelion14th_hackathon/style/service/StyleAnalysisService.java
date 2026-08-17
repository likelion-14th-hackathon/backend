package com.example.likelion14th_hackathon.style.service;

import com.example.likelion14th_hackathon.member.domain.Member;   
import com.example.likelion14th_hackathon.style.ai.OpenAiStyleAnalyzer;
import com.example.likelion14th_hackathon.style.entity.StyleType;
import com.example.likelion14th_hackathon.style.entity.UserStyle;
import com.example.likelion14th_hackathon.style.repository.UserStyleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StyleAnalysisService {

    private final OpenAiStyleAnalyzer analyzer;
    private final UserStyleRepository userStyleRepository;

    /**
     * 사진 URL 목록을 분석해 회원의 스타일을 갱신한다.
     * 사진이 추가될 때마다 전체를 재분석하는 방식.
     *
     * @return 갱신된 스타일 목록 (분석 실패 시 기존 스타일 유지하고 빈 목록 반환)
     */
    @Transactional
    public List<StyleType> analyzeAndUpdate(Member member, List<String> photoUrls) {
        // 1. AI 분석
        List<StyleType> styles = analyzer.analyze(photoUrls);

        // 2. 결과가 없으면 기존 스타일 유지 (덮어쓰지 않음)
        if (styles.isEmpty()) {
            log.info("스타일 분석 결과가 없어 기존 스타일을 유지합니다. memberId={}",
                    member.getId());   // TODO: Member PK getter 확인
            return List.of();
        }

        // 3. 기존 스타일 제거 후 새 결과 저장 (전체 교체)
        userStyleRepository.deleteByMember(member);
        userStyleRepository.flush();   // 삭제를 먼저 반영해 순서 보장

        for (StyleType style : styles) {
            userStyleRepository.save(new UserStyle(member, style));
        }

        log.info("회원 스타일 갱신 완료. memberId={}, styles={}",
                member.getId(), styles);
        return styles;
    }

    /** 현재 저장된 회원 스타일 조회 */
    @Transactional(readOnly = true)
    public List<StyleType> getMemberStyles(Member member) {
        return userStyleRepository.findByMember(member).stream()
                .map(UserStyle::getStyleType)
                .toList();
    }
}