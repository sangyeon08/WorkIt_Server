package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private String websiteUrl;

}