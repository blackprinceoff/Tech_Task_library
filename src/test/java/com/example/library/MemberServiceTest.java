package com.example.library;

import com.example.library.entity.Member;
import com.example.library.repository.MemberRepository;
import com.example.library.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    private MemberRepository memberRepository;
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        memberService = new MemberService(memberRepository);
    }

    @Test
    void shouldCreateMemberWithTodayDate() {
        Member member = new Member();
        member.setName("Test User");

        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member saved = memberService.createMember(member);

        assertThat(saved.getMembershipDate()).isEqualTo(LocalDate.now());
        verify(memberRepository).save(saved);
    }

    @Test
    void shouldReturnAllMembers() {
        List<Member> list = Arrays.asList(new Member(), new Member());
        when(memberRepository.findAll()).thenReturn(list);

        List<Member> result = memberService.getAllMembers();
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldReturnMemberById() {
        Member member = new Member();
        member.setId(1L);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member result = memberService.getMemberById(1L);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void shouldUpdateMemberName() {
        Member existing = new Member();
        existing.setId(1L);
        existing.setName("Old Name");

        Member updated = new Member();
        updated.setName("New Name");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Member result = memberService.updateMember(1L, updated);
        assertThat(result.getName()).isEqualTo("New Name");
    }

    @Test
    void shouldDeleteMemberIfNoBorrowedBooks() {
        Member member = new Member();
        member.setId(1L);
        member.setBorrowedBooks(Collections.emptyList());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.deleteMember(1L);
        verify(memberRepository).delete(member);
    }

    @Test
    void shouldThrowIfMemberHasBorrowedBooks() {
        Member member = new Member();
        member.setId(1L);
        member.setBorrowedBooks(List.of(mock(com.example.library.entity.BorrowedBook.class)));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.deleteMember(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot be deleted");
    }

    @Test
    void shouldThrowIfMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Member not found");
    }
}
