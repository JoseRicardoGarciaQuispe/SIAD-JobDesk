package gob.presidencia.siad.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IExportarDataService;
import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/exportar-data")
public class ExportarDataController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExportarDataController.class);
	
	@Autowired
	private IExportarDataService exportarDataService;
		
	@GetMapping
	public String inicioVista(Model model) {
		try {
			return "paginas/jobdesk/exportar-data/principal";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/reporte/{id}")
	public String verDocumento(Model model, @PathVariable("id") String id) throws Exception {
		try {
			if(StringUtils.isBlank(id)) throw new Exception("Error numero de solicitud es nulo o vacio");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", exportarDataService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("reporte", exportarDataService.obtenerReporte(id));
			return "paginas/jobdesk/exportar-data/modalVerReporte";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
}
