package com.shopmax.repository;

import com.shopmax.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	//select * from member where email = ?
	Member findByEmail(String email);
}
