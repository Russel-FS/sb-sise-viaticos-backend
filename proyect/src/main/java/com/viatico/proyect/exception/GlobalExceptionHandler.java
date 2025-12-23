package com.viatico.proyect.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundException(NoHandlerFoundException ex, Model model) {
        model.addAttribute("error", "La página que buscas no existe.");
        return "error/404";
    }
}
