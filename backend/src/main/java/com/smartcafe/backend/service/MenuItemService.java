package com.smartcafe.backend.service;

import com.smartcafe.backend.entity.MenuItem;
import com.smartcafe.backend.exception.ResourceNotFoundException;
import com.smartcafe.backend.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findByDeletedFalse();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        if ("All".equalsIgnoreCase(category)) {
            return menuItemRepository.findByDeletedFalse();
        }
        return menuItemRepository.findByCategoryAndDeletedFalse(category);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
    }

    @Transactional
    public MenuItem createMenuItem(MenuItem menuItem) {
        menuItem.setDeleted(false);
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItem details) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));

        menuItem.setName(details.getName());
        menuItem.setDescription(details.getDescription());
        menuItem.setPrice(details.getPrice());
        menuItem.setCategory(details.getCategory());
        menuItem.setImageUrl(details.getImageUrl());
        menuItem.setAvailable(details.isAvailable());
        menuItem.setPrepTimeMinutes(details.getPrepTimeMinutes());

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        
        // Soft delete so existing orders remain intact
        menuItem.setDeleted(true);
        menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem toggleAvailability(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        
        menuItem.setAvailable(!menuItem.isAvailable());
        return menuItemRepository.save(menuItem);
    }
}
