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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IFutService;
import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/asistencia/fut")
public class FUTController {
	
	private static final Logger logger = LoggerFactory.getLogger(FUTController.class);
	private static final String MESSAGE_ERROR_DEFAULT = "Ocurrio un error. Por favor, consultar con el administrador del sistema.";
	
	@Autowired
	private IFutService futService;
	
	@GetMapping()
	public String homeView(Model model) {	
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", futService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("listaEstados", futService.listarEstados());
			model.addAttribute("listaMotivos", futService.listarMotivos());
			
			return "paginas/jobdesk/asistencia/fut/principal";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	
	@PostMapping
	public String homeViewPost(Model model, @RequestParam(name = "message", required = false) String message) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", futService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("listaEstados", futService.listarEstados());
			model.addAttribute("listaMotivos", futService.listarMotivos());
			model.addAttribute("messageSystem", message);
			return "paginas/jobdesk/asistencia/fut/principal";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/nuevo")
	public String registrarFut(Model model) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", futService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("listaMotivos", futService.listarMotivos());
			return "paginas/jobdesk/asistencia/fut/registro";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/editar/{pk}")
	public String editarFut(Model model, @PathVariable("pk") String pk) {
		try {
			
			logger.info("id -> " + pk);
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<2) throw new Exception("Error PK no contiene alguno de los campos principales");
				
			Fut fut = futService.editarFut(tokens[0], tokens[1]);
	        // Imprimir los valores de
	        logger.info("just: " + fut.getJust());
	        logger.info("CmotCodmot: " + fut.getCmotCodmot());
	        logger.info("motivo: " + fut.getMotivo());
	        
	        model.addAttribute("detalleFut", fut);
			
			//model.addAttribute("detalleFut", futService.editarFut(tokens[0], tokens[1])); 
	        model.addAttribute("listaMotivos", futService.listarMotivos());
			model.addAttribute("listaOficinas", futService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			return "paginas/jobdesk/asistencia/fut/editar";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/empleados/{codigoOficina}")
	public String listarEmpleadosPorOficina(Model model, @PathVariable("codigoOficina") String codigoOficina) {
		try {
			if (StringUtils.isBlank(codigoOficina)) throw new Exception("Error codigo de oficina es nulo o vacio");
				model.addAttribute("listaEmpleados", futService.listarEmpleadosPorOficina(codigoOficina));
				logger.info(" -- MODAL DE EMPLEADOS --");
			return "paginas/jobdesk/asistencia/fut/modalBuscarEmpleados";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/aprobaciones/{pk}")
	public String verDocumento(Model model, @PathVariable("pk") String pk) {
		try {
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<2) throw new Exception("Error PK no contiene alguno de los campos principales");
			
			model.addAttribute("listaFlujos", futService.listarFlujoAprobacion(tokens[0], tokens[1]));
			return "paginas/jobdesk/asistencia/fut/modalVerHistorial";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}

}
