package de.hoehne.stackit.test.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class RedirectMetrics {
    
    @GetMapping("/metrics")
    public ModelAndView redirectWithUsingRedirectPrefix(ModelMap model) {
        model.addAttribute("attribute", "redirectWithRedirectPrefix");
        return new ModelAndView("redirect:/actuator/prometheus", model);
    }
}