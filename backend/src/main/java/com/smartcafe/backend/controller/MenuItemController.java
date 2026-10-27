package com.smartcafe.backend.controller;

import com.smartcafe.backend.entity.MenuItem;
import com.smartcafe.backend.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemService menuItemService;

    // Public API: Get all active menu items
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    // Public API: Get items by category
    @GetMapping("/menu/category/{category}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByCategory(category));
    }

    // Admin API: Create a menu item
    @PostMapping("/admin/menu")
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem created = menuItemService.createMenuItem(menuItem);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Admin API: Update a menu item
    @PutMapping("/admin/menu/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItemDetails) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, menuItemDetails));
    }

    // Admin API: Delete a menu item (Soft Delete)
    @DeleteMapping("/admin/menu/{id}")
    public ResponseEntity<Map<String, String>> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok(Map.of("message", "Menu item deleted successfully."));
    }

    // Admin API: Toggle item availability
    @PatchMapping("/admin/menu/{id}/toggle-availability")
    public ResponseEntity<MenuItem> toggleAvailability(@PathVariable Long id) {
        return ResponseEntity.ok(menuItemService.toggleAvailability(id));
    }
}
