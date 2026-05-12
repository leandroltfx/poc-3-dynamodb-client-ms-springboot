package br.com.poc_3_dynamodb_client_ms_springboot.service;

import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductResponseDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.entity.ProductEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final DynamoDbTable<ProductEntity> table;

    public ProductService(DynamoDbClient client) {

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient
                .builder()
                .dynamoDbClient(client)
                .build();

        this.table = enhancedClient.table(
                "products-default-table",
                TableSchema.fromBean(ProductEntity.class)
        );
    }

    public void save(ProductDTO productDTO) {

        ProductEntity productEntity = new ProductEntity();

        productEntity.setId(UUID.randomUUID().toString());
        productEntity.setCategory(productDTO.category());
        productEntity.setName(productDTO.name());
        productEntity.setPrice(productDTO.price());

        table.putItem(productEntity);
    }

    public List<ProductResponseDTO> findAllByCategory(String category) {
        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();

        QueryConditional condition =
                QueryConditional.keyEqualTo(
                        Key.builder()
                                .partitionValue(category)
                                .build()
                );

        table.query(condition)
                .items()
                .forEach(productEntity -> {
                    ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                            productEntity.getCategory(),
                            productEntity.getId(),
                            productEntity.getName(),
                            productEntity.getPrice()
                    );

                    productResponseDTOS.add(productResponseDTO);
                });

        return productResponseDTOS;
    }

    public List<ProductResponseDTO> scanProducts() {

        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();

        table.scan().items().forEach(productEntity -> {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                    productEntity.getCategory(),
                    productEntity.getId(),
                    productEntity.getName(),
                    productEntity.getPrice()
            );

            productResponseDTOS.add(productResponseDTO);
        });

        return productResponseDTOS;
    }
}
