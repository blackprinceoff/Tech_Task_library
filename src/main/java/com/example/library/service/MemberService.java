package com.example.library.service;

import com.example.library.entity.Member;
import com.example.library.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Value("${library.borrow.limit:10}")
    private int borrowLimit;

    public Member createMember(@Valid Member member) {
        member.setMembershipDate(LocalDate.now());
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public Member updateMember(Long id, @Valid Member updated) {
        return memberRepository.findById(id)
                .map(m -> {
                    m.setName(updated.getName());
                    return memberRepository.save(m);
                })
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public void deleteMember(Long id) {
        Member member = getMemberById(id);
        if (!member.getBorrowedBooks().isEmpty()) {
            throw new IllegalStateException("Member has borrowed books and cannot be deleted");
        }
        memberRepository.delete(member);
    }
}
