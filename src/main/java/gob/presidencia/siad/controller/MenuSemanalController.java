package gob.presidencia.siad.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Semana;
import gob.presidencia.siad.service.IMenuSemanalService;
import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/alimentacion/menu")
public class MenuSemanalController {
	
	private static final Logger logger = LoggerFactory.getLogger(MenuSemanalController.class);
	
	@Autowired
	private IMenuSemanalService menuSemanalService;
	
	@GetMapping
	public String inicioVista(Model model) {
		try {
			List<Parametro> listaAnios = menuSemanalService.listarAnios();
	        model.addAttribute("listaAnios", listaAnios);
			
			// Obtener las semanas del primer año en la lista, si hay al menos un año
	        if (!listaAnios.isEmpty()) {
	            List<Semana> listaSemanas = menuSemanalService.listarSemanas(listaAnios.get(0).getCodigo());
	            model.addAttribute("listaSemanas", listaSemanas);
	        }

			return "paginas/jobdesk/alimentacion/menu/principal";
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
}
