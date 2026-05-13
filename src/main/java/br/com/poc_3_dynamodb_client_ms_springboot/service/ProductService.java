package br.com.poc_3_dynamodb_client_ms_springboot.service;

import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductRequestDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductResponseDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.entity.ProductEntity;
import br.com.poc_3_dynamodb_client_ms_springboot.model.PriceOperatorEnum;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.math.BigDecimal;
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

    public void save(ProductRequestDTO productRequestDTO) {

        ProductEntity productEntity = new ProductEntity();

        productEntity.setId(UUID.randomUUID().toString());
        productEntity.setCategory(productRequestDTO.category());
        productEntity.setName(productRequestDTO.name());
        productEntity.setPrice(productRequestDTO.price());

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

    public List<ProductResponseDTO> findByCategoryAndPrice(
            String category,
            BigDecimal price,
            PriceOperatorEnum priceOperatorEnum
    ) {

        List<ProductResponseDTO> productResponseDTOS = new ArrayList<>();
        DynamoDbIndex<ProductEntity> priceIndex = table.index("products-price-index");
        QueryConditional condition;
        Key key = Key.builder()
                .partitionValue(category)
                .sortValue(price)
                .build();

        switch (priceOperatorEnum) {
            case EQ -> condition = QueryConditional.keyEqualTo(key);
            case LT -> condition = QueryConditional.sortLessThan(key);
            case LTE -> condition = QueryConditional.sortLessThanOrEqualTo(key);
            case GT -> condition = QueryConditional.sortGreaterThan(key);
            case GTE -> condition = QueryConditional.sortGreaterThanOrEqualTo(key);
            default -> throw new IllegalArgumentException("Operador inválido");
        }

        priceIndex.query(condition)
                .stream()
                .flatMap(page -> page.items().stream())
                .forEach(product -> {

                    ProductResponseDTO dto =
                            new ProductResponseDTO(
                                    product.getCategory(),
                                    product.getId(),
                                    product.getName(),
                                    product.getPrice()
                            );

                    productResponseDTOS.add(dto);
                });

        return productResponseDTOS;
    }
}
