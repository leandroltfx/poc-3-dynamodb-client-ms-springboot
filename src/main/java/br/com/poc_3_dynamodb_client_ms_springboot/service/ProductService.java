package br.com.poc_3_dynamodb_client_ms_springboot.service;

import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductResponseDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.model.Product;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final DynamoDbTable<Product> table;

    public ProductService(DynamoDbClient client) {

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient
                .builder()
                .dynamoDbClient(client)
                .build();

        this.table = enhancedClient.table(
                "products-default-table",
                TableSchema.fromBean(Product.class)
        );
    }

    public Product find(String id, String category) {
        return table.getItem(
                Key.builder()
                        .partitionValue(id)
                        .sortValue(category)
                        .build()
        );
    }

    public void save(ProductDTO productDTO) {

        Product product = new Product();

        product.setId(UUID.randomUUID().toString());
        product.setCategory(productDTO.category());
        product.setName(productDTO.name());

        table.putItem(product);
    }

    // Scan (verificação)
    public List<ProductResponseDTO> getAll() {

        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();

        table.scan().items().forEach(product -> {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                    product.getId(),
                    product.getCategory(),
                    product.getCategory()
            );

            productResponseDTOS.add(productResponseDTO);
        });

        return productResponseDTOS;
    }
}
