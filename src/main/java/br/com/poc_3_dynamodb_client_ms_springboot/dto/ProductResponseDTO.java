package br.com.poc_3_dynamodb_client_ms_springboot.dto;

public record ProductResponseDTO(
        String id,
        String category,
        String name
) {
}
