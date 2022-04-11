package com.kitchenforce.controller.product

import com.kitchenforce.domain.products.entities.Product
import com.kitchenforce.service.product.ProductService
import org.springframework.web.bind.annotation.* // ktlint-disable no-wildcard-imports
import javax.validation.Valid

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val service: ProductService,
) {
    @GetMapping
    fun index(): List<Product> {
        return service.findAll()
    }

    @PostMapping
    fun create(@Valid @RequestBody vo: Product): Product {
        return service.create(vo)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody data: Product): Product {
        return service.update(id, data)
    }
}