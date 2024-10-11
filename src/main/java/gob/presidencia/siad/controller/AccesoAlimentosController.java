package gob.presidencia.siad.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IAccesoAlimentosService;
import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/alimentacion/acceso-alimentos")
public class AccesoAlimentosController {
	
private static final Logger logger = LoggerFactory.getLogger(AccesoAlimentosController.class);
	
	@Autowired
	private IAccesoAlimentosService accesoAlimentosService;
	
	@GetMapping
	public String inicioVista(Model model) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", accesoAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			return "paginas/jobdesk/alimentacion/accesoAlimentos/principal";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
}
