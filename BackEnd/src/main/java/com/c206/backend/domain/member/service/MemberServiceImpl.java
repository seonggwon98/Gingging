package com.c206.backend.domain.member.service;

import com.c206.backend.domain.achievement.dto.response.MemberAchievementListResponseDto;
import com.c206.backend.domain.achievement.entity.Achievement;
import com.c206.backend.domain.achievement.entity.MemberAchievement;
import com.c206.backend.domain.achievement.repository.AchievementRepository;
import com.c206.backend.domain.achievement.repository.MemberAchievementRepository;
import com.c206.backend.domain.achievement.service.MemberAchievementService;
import com.c206.backend.domain.member.entity.Member;
import com.c206.backend.domain.member.entity.MemberInfo;
import com.c206.backend.domain.member.exception.MemberError;
import com.c206.backend.domain.member.exception.MemberException;
import com.c206.backend.domain.member.repository.MemberInfoRepository;
import com.c206.backend.domain.pet.entity.MemberPet;
import com.c206.backend.domain.pet.entity.Pet;
import com.c206.backend.domain.pet.exception.PetError;
import com.c206.backend.domain.pet.exception.PetException;
import com.c206.backend.domain.pet.repository.MemberPetRepository;
import com.c206.backend.domain.pet.repository.PetRepository;
import com.c206.backend.domain.pet.service.MemberPetService;
import com.c206.backend.domain.pet.service.MemberPetServiceImpl;
import com.c206.backend.global.jwt.CustomUserDetailsService;
import com.c206.backend.domain.member.dto.request.SignInRequestDto;
import com.c206.backend.domain.member.dto.request.SignUpRequestDto;
import com.c206.backend.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final MemberInfoRepository memberInfoRepository;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final MemberPetService memberPetService;
    private final MemberAchievementService memberAchievement;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public MemberServiceImpl(MemberRepository memberRepository, MemberInfoRepository memberInfoRepository, AchievementRepository achievementRepository, MemberAchievementRepository memberAchievementRepository, MemberPetService memberPetService, MemberAchievementService memberAchievement, MemberAchievementService memberAchievement1, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.memberInfoRepository = memberInfoRepository;
        this.achievementRepository = achievementRepository;
        this.memberAchievementRepository = memberAchievementRepository;
        this.memberPetService = memberPetService;
        this.memberAchievement = memberAchievement1;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Boolean signUpProcess(SignUpRequestDto signupDto) {

        String email = signupDto.getEmail();
        String password = signupDto.getPassword();

        Optional<Member> isExist = memberRepository.findByEmail(email);

        if(isExist.isPresent()){
            System.out.println("이미 존재하는 사용자입니다.");
            return false;
        }else{
            System.out.println("회원가입 가능");
            //회원가입
            signupDto.setPassword(bCryptPasswordEncoder.encode(password));
            memberRepository.save(signupDto.toEntity());
            System.out.println("회원가입 성공");

            Optional<Member> nowMember = memberRepository.findByEmail(email);
            if(nowMember.isEmpty()){
                return false;
            }

            System.out.println("기본펫 지급시작");
            //기본펫 1번 지급
            Member member = memberRepository.findById(nowMember.get().getId()).orElseThrow(()
                    -> new MemberException(MemberError.NOT_FOUND_MEMBER));
            memberPetService.provideBasePet(member);
            System.out.println("기본펫 지급성공");

            System.out.println("회원정보 지정시작");
            //회원-정보 테이블에 기본사항 지정하기
            MemberInfo newMemberInfo= MemberInfo.builder()
                    .member(member)
                    .profilePetId(null)
                    .exp(0)
                    .currency(0)
                    .build();
            memberInfoRepository.save(newMemberInfo);
            System.out.println("회원정보 지정성공");

            System.out.println("회원업적 지정시작");
            //회원-업적 테이블에 기본사항 지정하기
            List<Achievement> achievementList = achievementRepository.findAll();

            for (Achievement achievement : achievementList) {
                MemberAchievement newMemberAchievement = MemberAchievement.builder()
                        .member(member)
                        .achievement(achievement)
                        .progress(0)
                        .goalMultiply(0)
                        .build();
                memberAchievementRepository.save(newMemberAchievement);
            }
            System.out.println("회원업적 지정성공");

            return true;
        }
    }

    @Override
    public Boolean signInProcess(SignInRequestDto signInDto) {

        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(memberRepository);

        System.out.println("여기는 MemberServiceImpl");
        if(bCryptPasswordEncoder.matches(signInDto.getPassword(), customUserDetailsService.loadUserByUsername(signInDto.getUsername()).getPassword())){

            customUserDetailsService.loadUserByUsername(signInDto.getUsername());

            return true;
        }
        return false;
    }

    @Override
    public Boolean emailDupCheck(String email) {
        return memberRepository.existsByEmail(email);
    }



}