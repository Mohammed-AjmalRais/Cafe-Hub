package com.smartcafe.backend.controller;

import com.smartcafe.backend.entity.MenuItem;
import com.smartcafe.backend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MenuItemController {

    private final MenuItemRepository menuItemRepository;

    // Public API: Get all menu items
    @GetMapping("/menu")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemRepository.findByDeletedFalse());
    }

    // Public API: Get items by category
    @GetMapping("/menu/category/{category}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable String category) {
        if ("All".equalsIgnoreCase(category)) {
            return ResponseEntity.ok(menuItemRepository.findByDeletedFalse());
        }
        return ResponseEntity.ok(menuItemRepository.findByCategoryAndDeletedFalse(category));
    }

    // Admin API: Create a menu item
    @PostMapping("/admin/menu")
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        return ResponseEntity.ok(menuItemRepository.save(menuItem));
    }

    // Admin API: Update a menu item
    @PutMapping("/admin/menu/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable Long id, @RequestBody MenuItem menuItemDetails) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));

        menuItem.setName(menuItemDetails.getName());
        menuItem.setDescription(menuItemDetails.getDescription());
        menuItem.setPrice(menuItemDetails.getPrice());
        menuItem.setCategory(menuItemDetails.getCategory());
        menuItem.setImageUrl(menuItemDetails.getImageUrl());
        menuItem.setAvailable(menuItemDetails.isAvailable());
        menuItem.setPrepTimeMinutes(menuItemDetails.getPrepTimeMinutes());

        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updatedItem);
    }

    // Admin API: Delete a menu item (Soft Delete to preserve references in orders)
    @DeleteMapping("/admin/menu/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        
        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);
        return ResponseEntity.ok().body("Menu item deleted successfully.");
    }

    // Admin API: Toggle item availability (In-stock / Out of stock)
    @PatchMapping("/admin/menu/{id}/toggle-availability")
    public ResponseEntity<MenuItem> toggleAvailability(@PathVariable Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found: " + id));
        
        menuItem.setAvailable(!menuItem.isAvailable());
        MenuItem updatedItem = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updatedItem);
    }

    // Admin API: Reset menu to default seeded INR items
    @PostMapping("/admin/menu/reset")
    public ResponseEntity<?> resetMenuToDefaults() {
        // Soft-delete current active menu items so we do not break foreign keys on past orders
        List<MenuItem> active = menuItemRepository.findByDeletedFalse();
        for (MenuItem item : active) {
            item.setDeleted(true);
        }
        menuItemRepository.saveAll(active);
        List<MenuItem> defaultItems = java.util.Arrays.asList(
            // Coffee
            MenuItem.builder()
                    .name("Signature Hazelnut Frappé")
                    .description("Rich, blended espresso milkshake flavored with roasted hazelnuts and topped with whipped cream.")
                    .price(220.00)
                    .category("Coffee")
                    .imageUrl("https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(4)
                    .build(),
            MenuItem.builder()
                    .name("Caramel Macchiato")
                    .description("Creamy espresso with vanilla-flavored syrup, milk, and a caramel drizzle topping.")
                    .price(240.00)
                    .category("Coffee")
                    .imageUrl("https://images.unsplash.com/photo-1541167760496-1628856ab772?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(5)
                    .build(),
            MenuItem.builder()
                    .name("Vietnamese Iced Coffee")
                    .description("Strong drip espresso poured over ice and sweetened with condensed milk, a sweet tropical boost.")
                    .price(190.00)
                    .category("Coffee")
                    .imageUrl("https://images.unsplash.com/photo-1461016951428-745141573f4d?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(3)
                    .build(),

            // Tea
            MenuItem.builder()
                    .name("Matcha Mint Cooler")
                    .description("Velvety stone-ground matcha whisked with fresh garden mint, ice, and organic oat milk.")
                    .price(210.00)
                    .category("Tea")
                    .imageUrl("https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(4)
                    .build(),
            MenuItem.builder()
                    .name("Hibiscus Iced Tea")
                    .description("Hand-steeped red hibiscus blossoms infused with lemongrass, elderberry, and a touch of raw honey.")
                    .price(160.00)
                    .category("Tea")
                    .imageUrl("https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(3)
                    .build(),
            MenuItem.builder()
                    .name("Tropical Passion Fruit Mojito")
                    .description("Sparkling soda with fresh passion fruit pulp, crushed lime wedges, and wild mint leaves.")
                    .price(180.00)
                    .category("Tea")
                    .imageUrl("https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(4)
                    .build(),

            // Snacks
            MenuItem.builder()
                    .name("The Hub Club Sandwich")
                    .description("Double-decker toasted bread loaded with grilled paneer, cucumber, tomatoes, house tropical pesto, and lettuce.")
                    .price(260.00)
                    .category("Snacks")
                    .imageUrl("https://images.unsplash.com/photo-1525351484163-7529414344d8?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(8)
                    .build(),
            MenuItem.builder()
                    .name("Avocado Sourdough Toast")
                    .description("Fresh crushed avocado, cherry tomatoes, and microgreens on toasted artisanal sourdough.")
                    .price(290.00)
                    .category("Snacks")
                    .imageUrl("https://images.unsplash.com/photo-1541532713592-79a0317b6b77?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(6)
                    .build(),
            MenuItem.builder()
                    .name("Continental Peri Peri Fries")
                    .description("Golden shoestring fries tossed in spicy peri-peri seasoning and served with garlic mayo dip.")
                    .price(180.00)
                    .category("Snacks")
                    .imageUrl("https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(5)
                    .build(),

            // Desserts
            MenuItem.builder()
                    .name("Double Chocolate Waffles")
                    .description("Belgian waffle loaded with hot chocolate fudge, milk chocolate chips, and premium vanilla ice cream.")
                    .price(240.00)
                    .category("Desserts")
                    .imageUrl("https://images.unsplash.com/photo-1562376502-6f769499c886?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(7)
                    .build(),
            MenuItem.builder()
                    .name("Mango Panna Cotta")
                    .description("Creamy Italian custard infused with real Alphonso mango purée and topped with mint leaves.")
                    .price(210.00)
                    .category("Desserts")
                    .imageUrl("https://images.unsplash.com/photo-1488477181946-6428a0291777?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(4)
                    .build(),

            // Combos
            MenuItem.builder()
                    .name("Tropical Sunrise Feast")
                    .description("Matcha Mint Cooler paired with our signature Avocado Sourdough Toast.")
                    .price(420.00)
                    .category("Combos")
                    .imageUrl("https://images.unsplash.com/photo-1498804103079-a6351b050096?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(8)
                    .build(),
            MenuItem.builder()
                    .name("Sweet Escape Waffle Combo")
                    .description("Signature Hazelnut Frappé paired with our Double Chocolate Waffle.")
                    .price(390.00)
                    .category("Combos")
                    .imageUrl("https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=500&auto=format&fit=crop&q=80")
                    .available(true)
                    .prepTimeMinutes(9)
                    .build()
        );
        menuItemRepository.saveAll(defaultItems);
        return ResponseEntity.ok().body("Menu reset to INR default catalog successfully.");
    }
}
