package com.heatingManagement;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomePageController
{

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HomePageController.class);

    @GetMapping("/home")
    public String homePage(Model model)
    {
        model.addAttribute("Welcome",
                "Heating management app" );
        return "home";
    }

}
