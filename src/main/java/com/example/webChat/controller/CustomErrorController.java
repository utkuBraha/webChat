package com.example.webChat.controller;




import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "The page you are looking for might have been removed.");
            } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("message", "There was an internal server error.");
            } else {
                model.addAttribute("message", "Unexpected error occurred.");
            }
        }
        return "error";  // error.html sayfasına yönlendirir
    }
}
