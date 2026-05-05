package com.vlad.buildrent.web.admin;

import com.vlad.buildrent.domain.Category;
import com.vlad.buildrent.domain.Equipment;
import com.vlad.buildrent.domain.EquipmentImage;
import com.vlad.buildrent.repository.CategoryRepository;
import com.vlad.buildrent.service.EquipmentService;
import com.vlad.buildrent.service.FileStorageService;
import com.vlad.buildrent.util.SlugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/equipment")
@RequiredArgsConstructor
public class AdminEquipmentController {

    private final CategoryRepository categoryRepository;
    private final EquipmentService equipmentService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public String list(@RequestParam(required = false, defaultValue = "0") int page, Model model) {
        Page<Equipment> result = equipmentService.adminPage(page, 20);
        model.addAttribute("page", result);
        return "admin/equipment-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Equipment eq = new Equipment();
        eq.setActive(true);
        eq.setQuantityTotal(1);
        model.addAttribute("equipment", eq);
        model.addAttribute("categories", categoryRepository.findAllByOrderBySortOrderAscNameAsc());
        return "admin/equipment-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Equipment eq = equipmentService.getById(id);
        model.addAttribute("equipment", eq);
        model.addAttribute("categories", categoryRepository.findAllByOrderBySortOrderAscNameAsc());
        return "admin/equipment-form";
    }

    @PostMapping("/save")
    @Transactional
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String name,
                       @RequestParam(required = false) String slug,
                       @RequestParam(required = false) String shortDescription,
                       @RequestParam(required = false) String description,
                       @RequestParam BigDecimal pricePerDay,
                       @RequestParam(required = false) BigDecimal deposit,
                       @RequestParam int quantityTotal,
                       @RequestParam(required = false) String brand,
                       @RequestParam(required = false) String model,
                       @RequestParam Long categoryId,
                       @RequestParam(required = false, defaultValue = "false") boolean active,
                       @RequestParam(required = false) MultipartFile image,
                       RedirectAttributes ra) {
        try {
            Equipment eq = id == null ? new Equipment() : equipmentService.getById(id);
            Category category = categoryRepository.findById(categoryId).orElseThrow();
            eq.setName(name);
            eq.setSlug((slug == null || slug.isBlank()) ? SlugUtil.slugify(name + "-" + (brand == null ? "" : brand)) : slug);
            eq.setShortDescription(shortDescription);
            eq.setDescription(description);
            eq.setPricePerDay(pricePerDay);
            eq.setDeposit(deposit);
            eq.setQuantityTotal(quantityTotal);
            eq.setBrand(brand);
            eq.setModel(model);
            eq.setCategory(category);
            eq.setActive(active);
            Equipment saved = equipmentService.save(eq);

            if (image != null && !image.isEmpty()) {
                String url = fileStorageService.store(image);
                EquipmentImage img = EquipmentImage.builder()
                        .equipment(saved)
                        .url(url)
                        .sortOrder(saved.getImages().size())
                        .main(saved.getImages().isEmpty())
                        .build();
                saved.getImages().add(img);
                equipmentService.save(saved);
            }

            ra.addFlashAttribute("success", "Збережено");
            return "redirect:/admin/equipment/" + saved.getId() + "/edit";
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return id == null ? "redirect:/admin/equipment/new" : "redirect:/admin/equipment/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/image/{imageId}/delete")
    @Transactional
    public String deleteImage(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes ra) {
        Equipment eq = equipmentService.getById(id);
        eq.getImages().removeIf(img -> {
            if (img.getId().equals(imageId)) {
                fileStorageService.delete(img.getUrl());
                return true;
            }
            return false;
        });
        if (eq.getImages().stream().noneMatch(EquipmentImage::isMain) && !eq.getImages().isEmpty()) {
            eq.getImages().get(0).setMain(true);
        }
        equipmentService.save(eq);
        ra.addFlashAttribute("success", "Фото видалено");
        return "redirect:/admin/equipment/" + id + "/edit";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            equipmentService.deactivate(id);
            ra.addFlashAttribute("success", "Обладнання деактивовано");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/equipment";
    }
}
