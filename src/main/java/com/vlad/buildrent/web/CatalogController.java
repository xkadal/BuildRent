package com.vlad.buildrent.web;

import com.vlad.buildrent.domain.Equipment;
import com.vlad.buildrent.service.AvailabilityService;
import com.vlad.buildrent.service.CategoryService;
import com.vlad.buildrent.service.EquipmentService;
import com.vlad.buildrent.service.EquipmentService.EquipmentFilter;
import com.vlad.buildrent.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final EquipmentService equipmentService;
    private final CategoryService categoryService;
    private final ReviewService reviewService;
    private final AvailabilityService availabilityService;

    @GetMapping("/catalog")
    public String list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "0") int page,
            Model model) {

        EquipmentFilter filter = new EquipmentFilter(q, category, minPrice, maxPrice, brand);
        Page<Equipment> result = equipmentService.search(filter, page, 12, sort);

        model.addAttribute("page", result);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", equipmentService.findDistinctBrands());
        model.addAttribute("filter", filter);
        model.addAttribute("sort", sort);
        model.addAttribute("activeCategory", category);
        return "catalog/list";
    }

    @GetMapping("/category/{slug}")
    public String category(@PathVariable String slug,
                           @RequestParam(required = false, defaultValue = "0") int page,
                           Model model) {
        var cat = categoryService.findBySlug(slug);
        EquipmentFilter filter = new EquipmentFilter(null, slug, null, null, null);
        Page<Equipment> result = equipmentService.search(filter, page, 12, "newest");
        model.addAttribute("page", result);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("brands", equipmentService.findDistinctBrands());
        model.addAttribute("filter", filter);
        model.addAttribute("sort", "newest");
        model.addAttribute("activeCategory", slug);
        model.addAttribute("categoryHeading", cat);
        return "catalog/list";
    }

    @GetMapping("/catalog/{slug}")
    public String details(@PathVariable String slug,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                          Model model) {
        Equipment equipment = equipmentService.findBySlug(slug);

        LocalDate today = LocalDate.now();
        if (start == null) start = today.plusDays(1);
        if (end == null) end = today.plusDays(3);

        int available = availabilityService.availableQuantity(equipment.getId(), start, end);
        int days = AvailabilityService.daysBetween(start, end);

        model.addAttribute("equipment", equipment);
        model.addAttribute("reviews", reviewService.approvedFor(equipment.getId()));
        model.addAttribute("rating", reviewService.summary(equipment.getId()));
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("availableQty", available);
        model.addAttribute("days", days);
        return "catalog/details";
    }
}
