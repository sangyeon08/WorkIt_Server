package com.jubilee.workit.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BulkJobIdsRequest {
    private List<Long> jobIds;
}
