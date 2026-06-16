package com.jubilee.workit.repository;

import com.jubilee.workit.entity.JobPosting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    // 근처 공고 검색
    @Query(nativeQuery = true, value =
            "SELECT j.* FROM job_postings j " +
                    "JOIN locations l ON l.id = j.location_id " +
                    "WHERE (6371 * ACOS(" +
                    "  COS(RADIANS(:lat)) * COS(RADIANS(l.latitude)) * " +
                    "  COS(RADIANS(l.longitude) - RADIANS(:lng)) + " +
                    "  SIN(RADIANS(:lat)) * SIN(RADIANS(l.latitude))" +
                    ")) <= :radius " +
                    "ORDER BY j.published_at DESC")
    Page<JobPosting> findNearby(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusKm,
            Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location WHERE j.hot = true ORDER BY j.publishedAt DESC")
    Page<JobPosting> findHotPostings(Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location ORDER BY j.publishedAt DESC")
    Page<JobPosting> findLatestPostings(Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location " +
            "WHERE j.location.id = :locationId ORDER BY j.publishedAt DESC")
    Page<JobPosting> findByLocationId(@Param("locationId") Long locationId, Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location " +
            "WHERE UPPER(j.durationType) = 'LONG_TERM' ORDER BY j.publishedAt DESC")
    Page<JobPosting> findLongTermPostings(Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location " +
            "WHERE UPPER(j.durationType) = 'SHORT_TERM' ORDER BY j.publishedAt DESC")
    Page<JobPosting> findShortTermPostings(Pageable pageable);

    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location " +
            "WHERE UPPER(j.title) LIKE UPPER(CONCAT('%', :q, '%')) OR UPPER(j.description) LIKE UPPER(CONCAT('%', :q, '%')) " +
            "ORDER BY j.publishedAt DESC")
    Page<JobPosting> search(@Param("q") String q, Pageable pageable);

    // 구인자: 내가 등록한 공고 목록
    @Query("SELECT j FROM JobPosting j LEFT JOIN FETCH j.company LEFT JOIN FETCH j.location " +
            "WHERE j.employer.id = :employerId ORDER BY j.publishedAt DESC")
    Page<JobPosting> findByEmployerId(@Param("employerId") Long employerId, Pageable pageable);

    Optional<JobPosting> findByIdAndEmployer_Id(Long id, Long employerId);
}
