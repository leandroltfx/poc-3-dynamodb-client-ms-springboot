package br.com.poc_3_dynamodb_client_ms_springboot.dto;

import java.math.BigDecimal;

public record ProductRequestDTO(
        String category,
        String name,
        BigDecimal price
) {
}
