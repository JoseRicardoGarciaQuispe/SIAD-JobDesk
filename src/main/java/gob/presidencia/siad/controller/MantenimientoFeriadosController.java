package gob.presidencia.siad.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import gob.presidencia.siad.model.security.UserDetails;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/asistencia/mantenimiento-feriado")
public class MantenimientoFeriadosController {
    private static final Logger logger = LoggerFactory.getLogger(MantenimientoFeriadosController.class);

    //@Autowired
    //private IMantenimientoFeriadosService mantenimientoFeriadosService;

    @GetMapping
    public String inicioVista(Model model){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return "paginas/jobdesk/asistencia/mantenimiento-feriados";
        } catch (Exception e){
            logger.error(e.getMessage(), e);
            model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
            return "paginas/error";
        }
    }

}
