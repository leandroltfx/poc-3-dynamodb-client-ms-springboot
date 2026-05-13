package br.com.poc_3_dynamodb_client_ms_springboot.entity;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;

@DynamoDbBean
@Data
public class ProductEntity {

    private String category;
    private String id;
    private String name;
    private BigDecimal price;

    @DynamoDbPartitionKey
    @DynamoDbSecondaryPartitionKey(indexNames = "products-price-index")
    public String getCategory() {
        return category;
    }

    @DynamoDbSortKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondarySortKey(indexNames = "products-price-index")
    public BigDecimal getPrice() {
        return price;
    }

}
