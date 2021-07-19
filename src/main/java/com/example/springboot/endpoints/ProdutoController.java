package com.example.springboot.endpoints;

import com.example.springboot.entity.ProdutoEntity;
import com.example.springboot.repository.ProdutoRepository;
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
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/produtos")
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

    @GetMapping("/produtos/{id}")
    public ResponseEntity<ProdutoEntity> getProduto(@PathVariable("id") Long id) {
        Optional<ProdutoEntity> produto = produtoRepository.findById(id);
        if(!produto.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        produto.get().add(linkTo(methodOn(ProdutoController.class).getAllProdutos()).withRel("Lista de Produtos"));
        return new ResponseEntity<ProdutoEntity>(produto.get(), HttpStatus.OK);
    }

    @PostMapping("/produtos")
    public ResponseEntity<ProdutoEntity> saveProduto(@RequestBody @Valid ProdutoEntity produto) {
        return new ResponseEntity<ProdutoEntity>(produtoRepository.save(produto), HttpStatus.CREATED);
    }

    @DeleteMapping("/produtos/{id}")
    public ResponseEntity<ProdutoEntity> deleteProduto(@PathVariable("id") Long id) {
        Optional<ProdutoEntity> produto = produtoRepository.findById(id);
        if(!produto.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        produtoRepository.delete(produto.get());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/produtos/{id}")
    public ResponseEntity<ProdutoEntity> updateProduto(@PathVariable("id") Long id, @RequestBody ProdutoEntity produto) {
        Optional<ProdutoEntity> produtoOptional = produtoRepository.findById(id);
        if(!produtoOptional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        produto.setId(produtoOptional.get().getId());
        return new ResponseEntity<ProdutoEntity>(produtoRepository.save(produto), HttpStatus.OK);
    }
}
