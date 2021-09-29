package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.AuthenticationService;
import com.codesoom.assignment.application.ProductService;
import com.codesoom.assignment.domain.Product;
import com.codesoom.assignment.dto.ProductData;
import com.codesoom.assignment.errors.NotValidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {
    private final AuthenticationService authenticationService;
    private final ProductService productService;

    public ProductController(AuthenticationService authenticationService, ProductService productService) {
        this.authenticationService = authenticationService;
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getProducts() {

        return productService.getProducts();

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated() and hasAuthority('USER')")
    public Product createProduct(Authentication authentication, @RequestBody @Valid ProductData source) {

        return productService.createProduct(source);

    }

    @GetMapping("{id}")
    public Product getProduct(@PathVariable Long id) {

        return productService.getProduct(id);

    }

    @RequestMapping(value = "{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public Product updateProduct(@RequestHeader("Authorization") String authorization ,@PathVariable Long id, @RequestBody @Valid ProductData source) {

        isValid(authorization);

        return productService.updateProduct(id, source);

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {

        isValid(authorization);

        productService.deleteProduct(id);

    }

    private void isValid(String authorization) {
        String accessToken = authorization.substring("Bearer ".length());

        if (authenticationService.parseToken(accessToken) == null) {
            throw new NotValidTokenException();
        }
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleMissingRequestHeaderException() {

    }

}

