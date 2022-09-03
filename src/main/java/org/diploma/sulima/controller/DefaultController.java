package org.diploma.sulima.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

    @RequestMapping("/admin")
    public String index() {
        return "index";
    }

    @RequestMapping("/")
    public String noAdmin() {
        return "no-admin";
    }
}
