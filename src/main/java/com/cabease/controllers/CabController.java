package com.cabease.controllers;
import com.cabease.models.Cab;
import com.cabease.services.CabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
import java.util.List;
@Controller
@RequestMapping("/admin/cabs")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class CabController {
    @Autowired
    private CabService cabService;
    @GetMapping
    public String listCabs(Model model) {
        List<Cab> cabs = cabService.findAllCabs();
        model.addAttribute("cabs", cabs);
        return "admin/cabs/list";
    }
    @GetMapping("/new")
    public String newCabForm(Model model) {
        model.addAttribute("cab", new Cab());
        return "admin/cabs/form";
    }
    @PostMapping
    public String saveCab(@Valid @ModelAttribute Cab cab, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/cabs/form";
        }
        try {
            cabService.saveCab(cab);
            redirectAttributes.addFlashAttribute("success", "Cab saved successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cabs";
    }
    @GetMapping("/{id}/edit")
    public String editCabForm(@PathVariable Long id, Model model) {
        Cab cab = cabService.findById(id).orElseThrow(() -> new RuntimeException("Cab not found"));
        model.addAttribute("cab", cab);
        return "admin/cabs/form";
    }
    @PostMapping("/{id}")
    public String updateCab(@PathVariable Long id, @Valid @ModelAttribute Cab cab, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/cabs/form";
        }
        cab.setId(id);
        try {
            cabService.updateCab(cab);
            redirectAttributes.addFlashAttribute("success", "Cab updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cabs";
    }
    @PostMapping("/{id}/delete")
    public String deleteCab(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cabService.deleteCab(id);
            redirectAttributes.addFlashAttribute("success", "Cab deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/cabs";
    }
}
