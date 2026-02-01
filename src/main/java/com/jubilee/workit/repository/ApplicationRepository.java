package com.jubilee.workit.repository;

import com.jubilee.workit.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPosting.id = :jobPostingId")
    Integer countByJobPostingId(@Param("jobPostingId") Long jobPostingId);

    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);
}