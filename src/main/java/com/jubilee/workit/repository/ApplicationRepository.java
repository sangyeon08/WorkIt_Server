package com.jubilee.workit.repository;

import com.jubilee.workit.entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPosting.id = :jobPostingId")
    Integer countByJobPostingId(@Param("jobPostingId") Long jobPostingId);

    Page<Application> findByUser_IdOrderByAppliedAtDesc(Long userId, Pageable pageable);

    boolean existsByUser_IdAndJobPosting_Id(Long userId, Long jobPostingId);

    long countByUser_Id(Long userId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosting j " +
            "LEFT JOIN FETCH j.company " +
            "LEFT JOIN FETCH j.location " +
            "WHERE a.user.id = :userId " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:q IS NULL OR UPPER(j.title) LIKE UPPER(CONCAT('%', :q, '%')) " +
            "     OR UPPER(j.description) LIKE UPPER(CONCAT('%', :q, '%')) " +
            "     OR UPPER(j.company.name) LIKE UPPER(CONCAT('%', :q, '%'))) " +
            "ORDER BY a.appliedAt DESC")
    Page<Application> searchMyApplications(@Param("userId") Long userId,
                                           @Param("status") String status,
                                           @Param("q") String q,
                                           Pageable pageable);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.jobPosting j " +
            "LEFT JOIN FETCH j.company " +
            "LEFT JOIN FETCH j.location " +
            "WHERE a.user.id = :userId " +
            "AND a.status IN :statuses " +
            "AND (:q IS NULL OR UPPER(j.title) LIKE UPPER(CONCAT('%', :q, '%')) " +
            "     OR UPPER(j.description) LIKE UPPER(CONCAT('%', :q, '%')) " +
            "     OR UPPER(j.company.name) LIKE UPPER(CONCAT('%', :q, '%'))) " +
            "ORDER BY a.appliedAt DESC")
    Page<Application> searchMyApplicationsByStatuses(@Param("userId") Long userId,
                                                     @Param("statuses") Collection<String> statuses,
                                                     @Param("q") String q,
                                                     Pageable pageable);

    // 구인자: 내 공고의 지원자 목록 (상태 필터 + 이메일 검색)
    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.user " +
            "JOIN FETCH a.jobPosting j " +
            "WHERE j.id = :jobId AND j.employer.id = :employerId " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:q IS NULL OR UPPER(a.user.email) LIKE UPPER(CONCAT('%', :q, '%'))) " +
            "ORDER BY a.appliedAt DESC")
    Page<Application> findByJobIdAndEmployerId(@Param("jobId") Long jobId,
                                               @Param("employerId") Long employerId,
                                               @Param("status") String status,
                                               @Param("q") String q,
                                               Pageable pageable);

    // 구인자: 내 전체 공고 지원자 목록
    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.user " +
            "JOIN FETCH a.jobPosting j " +
            "WHERE j.employer.id = :employerId " +
            "ORDER BY a.appliedAt DESC")
    Page<Application> findAllByEmployerId(@Param("employerId") Long employerId, Pageable pageable);

    Optional<Application> findByIdAndJobPosting_Employer_Id(Long id, Long employerId);
}
