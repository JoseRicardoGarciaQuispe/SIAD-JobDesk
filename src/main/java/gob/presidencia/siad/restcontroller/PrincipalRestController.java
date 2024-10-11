package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IExportarDataService;

@RestController
@RequestMapping("/rest/principal")
public class PrincipalRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(PrincipalRestController.class);
	
	@Autowired
	private IExportarDataService exportarDataService;
	
	@PutMapping("/cambiarAnio/{codigo}")
	public ResponseEntity<?> cambiarAnioEjecucion(@PathVariable String codigo){		
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> listaAnios = exportarDataService.listarAnios();
			Parametro nuevoAnio = null;
			for(Parametro anio : listaAnios) {
				if(anio.getCodigo().equals(codigo)) {
					nuevoAnio = anio;
					break;
				}
			}
			if(nuevoAnio==null) {
				List<String> errors = new ArrayList<>();
				errors.add("Año no encontrado");
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
			
			userDetails.setAnio(nuevoAnio.getCodigo());
			SecurityContext securityContext = SecurityContextHolder.getContext();
	        securityContext.setAuthentication(authentication);
	        
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/*
	@GetMapping("/logout") 
	public ResponseEntity<?> logout(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null) {
	    		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	    		logger.info("LOGOUT REQUEST: " + request.getRequestURI()  +", usuario: " + userDetails.getUsername());
	            new SecurityContextLogoutHandler().logout(request, response, authentication);
	        	SecurityContextHolder.clearContext();
	        	HttpSession session = request.getSession(true);
	            session.removeAttribute("SPRING_SECURITY_CONTEXT");
	        }
	        return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	*/

}
