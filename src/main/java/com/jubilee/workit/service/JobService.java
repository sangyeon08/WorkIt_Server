package com.jubilee.workit.service;

import com.jubilee.workit.dto.*;
import com.jubilee.workit.entity.*;
import com.jubilee.workit.repository.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class JobService {

    private final JobPostingRepository jobPostingRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;

    public JobService(JobPostingRepository jobPostingRepository,
                      BookmarkRepository bookmarkRepository,
                      ApplicationRepository applicationRepository,
                      UserRepository userRepository,
                      LocationRepository locationRepository,
                      CompanyRepository companyRepository,
                      CategoryRepository categoryRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
    }

    // 근처 공고 검색
    public PageResponse<JobCardDto> getNearby(double latitude, double longitude, double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findNearby(latitude, longitude, radiusKm, pageable);
        return PageResponse.of(result.map(this::toCard));
    }

    // 공고 상세 조회
    public JobDetailDto getJobDetail(Long jobId, Long userId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."));

        JobDetailDto dto = new JobDetailDto();
        dto.setId(job.getId());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());

        // 기업
        if (job.getCompany() != null) {
            CompanyDto companyDto = new CompanyDto();
            companyDto.setId(job.getCompany().getId());
            companyDto.setName(job.getCompany().getName());
            companyDto.setDescription(job.getCompany().getDescription());
            companyDto.setLogoUrl(job.getCompany().getLogoUrl());
            companyDto.setWebsiteUrl(job.getCompany().getWebsiteUrl());
            dto.setCompany(companyDto);
        }

        // 위치
        if (job.getLocation() != null) {
            LocationDto locationDto = new LocationDto();
            locationDto.setId(job.getLocation().getId());
            locationDto.setName(job.getLocation().getName());
            locationDto.setCity(job.getLocation().getCity());
            locationDto.setCountry(job.getLocation().getCountry());
            locationDto.setLatitude(job.getLocation().getLatitude());
            locationDto.setLongitude(job.getLocation().getLongitude());
            dto.setLocation(locationDto);
        }

        dto.setCompensationAmount(job.getCompensationAmount());
        dto.setCompensationType(job.getCompensationType());
        dto.setJobType(job.getJobType());
        dto.setDurationType(job.getDurationType());

        List<String> categoryNames = job.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        dto.setCategoryNames(categoryNames);

        dto.setImageUrl(job.getImageUrl());
        dto.setHot(job.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(job.getPublishedAt() != null && job.getPublishedAt().isAfter(dayAgo));

        if (userId != null) {
            boolean isBookmarked = bookmarkRepository.existsByUser_IdAndJobPosting_Id(userId, jobId);
            dto.setBookmarked(isBookmarked);
        } else {
            dto.setBookmarked(false);
        }

        dto.setPublishedAt(job.getPublishedAt());
        dto.setExpiresAt(job.getExpiresAt());

        Integer applicantCount = applicationRepository.countByJobPostingId(jobId);
        dto.setApplicantCount(applicantCount != null ? applicantCount : 0);

        return dto;
    }

    public PageResponse<JobCardDto> getHotPostings(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findHotPostings(p);
        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> getLatestPostings(int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.findLatestPostings(p);
        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> getPostings(String filter, Long locationId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result;

        if ("by_location".equalsIgnoreCase(filter) && locationId != null) {
            result = jobPostingRepository.findByLocationId(locationId, p);
        } else if ("long_term".equalsIgnoreCase(filter)) {
            result = jobPostingRepository.findLongTermPostings(p);
        } else if ("short_term".equalsIgnoreCase(filter)) {
            result = jobPostingRepository.findShortTermPostings(p);
        } else {
            result = jobPostingRepository.findLatestPostings(p);
        }

        return PageResponse.of(result.map(this::toCard));
    }

    public PageResponse<JobCardDto> search(String q, int page, int size) {
        if (q == null || q.isBlank()) {
            return getLatestPostings(page, size);
        }
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<JobPosting> result = jobPostingRepository.search(q.trim(), p);
        return PageResponse.of(result.map(this::toCard));
    }

    // ── 구인자 공고 관리 ──────────────────────────────────────────────────────

    public PageResponse<JobCardDto> getMyPostedJobs(Long employerId, int page, int size) {
        Pageable p = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return PageResponse.of(jobPostingRepository.findByEmployerId(employerId, p).map(this::toCard));
    }

    @Transactional
    public JobDetailDto createJob(Long employerId, JobCreateRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 필수입니다.");
        }
        if (req.getJobType() == null || req.getJobType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "고용 유형(jobType)은 필수입니다.");
        }
        if (req.getLocationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "지역(locationId)은 필수입니다.");
        }
        if (req.getCompanyId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기업(companyId)은 필수입니다.");
        }

        User employer = userRepository.findById(employerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Location location = locationRepository.findById(req.getLocationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 지역입니다."));

        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 기업입니다."));

        List<Category> categories = new ArrayList<>();
        if (req.getCategoryIds() != null && !req.getCategoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(req.getCategoryIds());
        }

        JobPosting job = new JobPosting();
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(location);
        job.setCompany(company);
        job.setEmployer(employer);
        job.setCompensationAmount(req.getCompensationAmount());
        job.setCompensationType(req.getCompensationType());
        job.setJobType(req.getJobType());
        job.setDurationType(req.getDurationType());
        job.setImageUrl(req.getImageUrl());
        job.setExpiresAt(req.getExpiresAt());
        job.setCategories(categories);

        return getJobDetail(jobPostingRepository.save(job).getId(), employerId);
    }

    @Transactional
    public JobDetailDto updateJob(Long jobId, Long employerId, JobUpdateRequest req) {
        JobPosting job = jobPostingRepository.findByIdAndEmployer_Id(jobId, employerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없거나 수정 권한이 없습니다."));

        if (req.getTitle() != null) job.setTitle(req.getTitle());
        if (req.getDescription() != null) job.setDescription(req.getDescription());
        if (req.getCompensationAmount() != null) job.setCompensationAmount(req.getCompensationAmount());
        if (req.getCompensationType() != null) job.setCompensationType(req.getCompensationType());
        if (req.getJobType() != null) job.setJobType(req.getJobType());
        if (req.getDurationType() != null) job.setDurationType(req.getDurationType());
        if (req.getImageUrl() != null) job.setImageUrl(req.getImageUrl());
        if (req.getExpiresAt() != null) job.setExpiresAt(req.getExpiresAt());
        if (req.getHot() != null) job.setHot(req.getHot());

        if (req.getLocationId() != null) {
            Location location = locationRepository.findById(req.getLocationId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 지역입니다."));
            job.setLocation(location);
        }

        if (req.getCompanyId() != null) {
            Company company = companyRepository.findById(req.getCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "존재하지 않는 기업입니다."));
            job.setCompany(company);
        }

        if (req.getCategoryIds() != null) {
            job.setCategories(categoryRepository.findAllById(req.getCategoryIds()));
        }

        return getJobDetail(jobPostingRepository.save(job).getId(), employerId);
    }

    @Transactional
    public void deleteJob(Long jobId, Long employerId) {
        JobPosting job = jobPostingRepository.findByIdAndEmployer_Id(jobId, employerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "공고를 찾을 수 없거나 삭제 권한이 없습니다."));
        jobPostingRepository.delete(job);
    }

    private JobCardDto toCard(JobPosting j) {
        JobCardDto dto = new JobCardDto();
        dto.setId(j.getId());
        dto.setTitle(j.getTitle());
        dto.setCompanyName(j.getCompany() != null ? j.getCompany().getName() : null);
        dto.setCompanyLogoUrl(j.getCompany() != null ? j.getCompany().getLogoUrl() : null);
        dto.setLocationName(j.getLocation() != null ? j.getLocation().getName() : null);
        dto.setCompensationAmount(j.getCompensationAmount());
        dto.setCompensationType(j.getCompensationType());
        List<String> cats = j.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        dto.setCategoryNames(cats);
        dto.setImageUrl(j.getImageUrl());
        dto.setHot(j.isHot());
        LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
        dto.setNew(j.getPublishedAt() != null && j.getPublishedAt().isAfter(dayAgo));
        dto.setPublishedAt(j.getPublishedAt());
        dto.setExpiresAt(j.getExpiresAt());
        return dto;
    }
}
