package com.vlad.buildrent.web.api;

import com.vlad.buildrent.service.CartService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApi {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<?> add(@RequestBody AddRequest req) {
        try {
            cartService.add(req.equipmentId(), req.quantity(), req.startDate(), req.endDate());
            return ResponseEntity.ok(Map.of(
                    "cartCount", cartService.totalQuantity(),
                    "message", "Додано в кошик"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody UpdateRequest req) {
        try {
            cartService.updateQuantity(id, req.quantity());
            return ResponseEntity.ok(Map.of("cartCount", cartService.totalQuantity()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> remove(@PathVariable long id) {
        cartService.remove(id);
        return ResponseEntity.ok(Map.of("cartCount", cartService.totalQuantity()));
    }

    public record AddRequest(
            @NotNull Long equipmentId,
            @Min(1) int quantity,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {}

    public record UpdateRequest(@Min(1) int quantity) {}
}
