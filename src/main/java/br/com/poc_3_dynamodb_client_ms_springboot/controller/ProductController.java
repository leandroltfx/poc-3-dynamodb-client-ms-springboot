package br.com.poc_3_dynamodb_client_ms_springboot.controller;

import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.dto.ProductResponseDTO;
import br.com.poc_3_dynamodb_client_ms_springboot.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public void createProduct(
            @RequestBody ProductDTO productDTO
    ) {
        productService.save(productDTO);
    }

    @GetMapping("query")
    public List<ProductResponseDTO> getProductsByCategory(
            @RequestParam("category") String category
    ) {
        return productService.findAllByCategory(category);
    }

    @GetMapping("scan")
    public List<ProductResponseDTO> scanProducts() {
        return productService.scanProducts();
    }

}
