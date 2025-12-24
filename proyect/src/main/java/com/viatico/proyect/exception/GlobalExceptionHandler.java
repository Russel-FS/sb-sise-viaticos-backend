package com.viatico.proyect.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("Error no controlado: ", ex);
        model.addAttribute("error", "Ha ocurrido un error inesperado en el sistema.");
        model.addAttribute("message", ex.getMessage());
        return "error/500";
    }

    @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
    public String handleNotFoundException(Exception ex, Model model) {
        model.addAttribute("error", "La página que buscas no existe.");
        return "error/404";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("errorToggle", true);
        redirectAttributes.addFlashAttribute("errorMsg",
                "El archivo es demasiado grande. El límite máximo permitido es 20MB.");

        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/";
    }
}
