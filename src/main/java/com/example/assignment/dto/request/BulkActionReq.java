package com.example.assignment.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkActionReq<T> {
    private List<T> items;
}
