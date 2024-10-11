package gob.presidencia.siad.controller;

import org.apache.commons.lang3.StringUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.INotasAlimentosService;
import gob.presidencia.siad.util.Constantes;

@Controller
@RequestMapping("/alimentacion/notas-alimentos")
public class NotasAlimentosController {
	
	private static final Logger logger = LoggerFactory.getLogger(NotasAlimentosController.class);
	
	@Autowired
	private INotasAlimentosService notasAlimentosService;
	
	@GetMapping
	public String inicioVista(Model model) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("listaTipoDocumentos", notasAlimentosService.listarTipoDocumentos());
			model.addAttribute("listaTipoNotas", notasAlimentosService.listarTipoNotas());
			model.addAttribute("listaEstados", notasAlimentosService.listarEstados());
			return "paginas/jobdesk/alimentacion/notasAlimentos/principal";
		} catch (Exception e) { 
			logger.error(e.getMessage(), e); 
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@PostMapping
	public String inicioVistaPost(Model model, @RequestParam(name = "message", required = false) String message) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("listaTipoDocumentos", notasAlimentosService.listarTipoDocumentos());
			model.addAttribute("listaTipoNotas", notasAlimentosService.listarTipoNotas());
			model.addAttribute("listaEstados", notasAlimentosService.listarEstados());
			model.addAttribute("messageSystem", message);
			return "paginas/jobdesk/alimentacion/notasAlimentos/principal";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}

	@GetMapping("/nuevo")
	public String registrarNotaVista(Model model) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			model.addAttribute("listaTipoRaciones", notasAlimentosService.listarTipoRaciones());
			model.addAttribute("listaMotivos", notasAlimentosService.listarTipoMotivos());
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			return "paginas/jobdesk/alimentacion/notasAlimentos/registro";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/editar/fragment/{pk}")
	public String editarNotaFragmentVista(Model model, @PathVariable("pk") String pk) {
		try {
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<3) throw new Exception("Error PK no contiene alguno de los campos principales");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			NotaAlimento notaAlimento = notasAlimentosService.obtenerNotaAlimento(tokens[0], tokens[1], tokens[2]);
			notaAlimento.setCusuUsureg(userDetails.getUsername());
			
			model.addAttribute("listaTipoRaciones", notasAlimentosService.listarTipoRaciones());
			model.addAttribute("listaMotivos", notasAlimentosService.listarTipoMotivos());
			model.addAttribute("notaAlimento", notaAlimento);
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("esAprobador", notasAlimentosService.esAprobador(notaAlimento));
			return "paginas/jobdesk/alimentacion/notasAlimentos/body-editar";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/nuevo/{pk}")
	public String copiarNotaVista(Model model, @PathVariable("pk") String pk) {
		try {
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<3) throw new Exception("Error PK no contiene alguno de los campos principales");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			NotaAlimento notaAlimento = notasAlimentosService.obtenerNotaAlimento(tokens[0], tokens[1], tokens[2]);
			notaAlimento.setCanoAnosol(null);
			notaAlimento.setCdocTipdoc(null);
			notaAlimento.setCsolNumsol(null);
			
			model.addAttribute("listaTipoRaciones", notasAlimentosService.listarTipoRaciones());
			model.addAttribute("listaMotivos", notasAlimentosService.listarTipoMotivos());
			model.addAttribute("notaAlimento", notaAlimento);
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			return "paginas/jobdesk/alimentacion/notasAlimentos/copiar";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/editar/{pk}")
	public String editarNotaVista(Model model, @PathVariable("pk") String pk) {
		try {
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<3) throw new Exception("Error PK no contiene alguno de los campos principales");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			NotaAlimento notaAlimento = notasAlimentosService.obtenerNotaAlimento(tokens[0], tokens[1], tokens[2]);
			notaAlimento.setCusuUsureg(userDetails.getUsername());
			
			model.addAttribute("listaTipoRaciones", notasAlimentosService.listarTipoRaciones());
			model.addAttribute("listaMotivos", notasAlimentosService.listarTipoMotivos());
			model.addAttribute("notaAlimento", notaAlimento);
			model.addAttribute("listaOficinas", notasAlimentosService.listarOficinas(userDetails.getAnio(), userDetails.getUsername()));
			model.addAttribute("esAprobador", notasAlimentosService.esAprobador(notaAlimento));
			return "paginas/jobdesk/alimentacion/notasAlimentos/editar";
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
			model.addAttribute("listaTipoRaciones", notasAlimentosService.listarTipoRaciones());
			model.addAttribute("listaEmpleados", notasAlimentosService.listarEmpleadosPorOficina(codigoOficina));
			return "paginas/jobdesk/alimentacion/notasAlimentos/modalAgregarRacion";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/flujos/{pk}")
	public String verDocumento(Model model, @PathVariable("pk") String pk) {
		try {
			if(StringUtils.isBlank(pk)) throw new Exception("Error numero de solicitud es nulo o vacio");
			String[] tokens = pk.split("-");
			if(tokens.length<3) throw new Exception("Error PK no contiene alguno de los campos principales");
			
			model.addAttribute("listaFlujos", notasAlimentosService.listarFlujoEstados(tokens[0], tokens[1], tokens[2]));
			return "paginas/jobdesk/alimentacion/notasAlimentos/modalVerFlujos";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
}
