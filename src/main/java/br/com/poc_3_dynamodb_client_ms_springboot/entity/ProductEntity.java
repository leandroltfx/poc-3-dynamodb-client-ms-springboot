package br.com.poc_3_dynamodb_client_ms_springboot.entity;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.math.BigDecimal;

@DynamoDbBean
@Data
public class ProductEntity {

    private String category;
    private String id;
    private String name;
    private BigDecimal price;

    @DynamoDbPartitionKey
    public String getCategory() {
        return category;
    }

    @DynamoDbSortKey
    public String getId() {
        return id;
    }

}
