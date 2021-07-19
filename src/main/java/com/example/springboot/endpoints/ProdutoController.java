package com.example.springboot.endpoints;

import com.example.springboot.entity.ProdutoEntity;
import com.example.springboot.repository.ProdutoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/produtos")
@Api(tags = {"Spring Boot REST API de Produtos"})
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    @ApiOperation("Retorna uma lista com todos os produtos.")
    public ResponseEntity<List<ProdutoEntity>> getAllProdutos() {
        List<ProdutoEntity> produtos = produtoRepository.findAll();
        if(produtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        produtos.forEach(produto -> {
            Long id = produto.getId();
            produto.add(linkTo(methodOn(ProdutoController.class).getProduto(id)).withSelfRel());
        });

        return new ResponseEntity<List<ProdutoEntity>>(produtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation("Retorna um produto, de acordo com o ID informado.")
    public ResponseEntity<ProdutoEntity> getProduto(
            @ApiParam("ID do Produto do tipo Long")
            @PathVariable("id") Long id) {
        Optional<ProdutoEntity> produto = produtoRepository.findById(id);
        if(!produto.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        produto.get().add(linkTo(methodOn(ProdutoController.class).getAllProdutos()).withRel("Lista de Produtos"));
        return new ResponseEntity<ProdutoEntity>(produto.get(), HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation("Cria um novo Produto no banco de dados")
    public ResponseEntity<ProdutoEntity> saveProduto(
            @ApiParam("Representação do objeto do tipo Produto")
            @RequestBody @Valid ProdutoEntity produto) {
        return new ResponseEntity<ProdutoEntity>(produtoRepository.save(produto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Deleta um Produto do banco de dados")
    public ResponseEntity<ProdutoEntity> deleteProduto(
            @ApiParam("Id do Produto do tipo Long")
            @PathVariable("id") Long id) {
        Optional<ProdutoEntity> produto = produtoRepository.findById(id);
        if(!produto.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        produtoRepository.delete(produto.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation("Deleta um Produto do banco de dados")
    public ResponseEntity<ProdutoEntity> updateProduto(
            @ApiParam("Id do Produto do tipo Long")
            @PathVariable("id") Long id,
            @ApiParam("Representação do objeto do tipo Produto")
            @RequestBody ProdutoEntity produto) {
        Optional<ProdutoEntity> produtoOptional = produtoRepository.findById(id);
        if(!produtoOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        produto.setId(produtoOptional.get().getId());
        return new ResponseEntity<ProdutoEntity>(produtoRepository.save(produto), HttpStatus.OK);
    }
}
