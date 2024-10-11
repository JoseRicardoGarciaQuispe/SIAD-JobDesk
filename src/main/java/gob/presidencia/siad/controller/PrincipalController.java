package gob.presidencia.siad.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import gob.presidencia.siad.exception.InvalidSession;
import gob.presidencia.siad.exception.LoginException;
import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.request.LoginRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IReferenceService;
import gob.presidencia.siad.service.IUserService;
import gob.presidencia.siad.util.Constantes;
import gob.presidencia.siad.util.SistemaDefault;
import gob.presidencia.siad.validator.LoginValidator;

@Controller
public class PrincipalController {
	
	private static final Logger logger = LoggerFactory.getLogger(PrincipalController.class);
	
	@Autowired
	private IReferenceService referenceService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private LoginValidator loginValidator;
	
	@GetMapping("/accessDenied") 
	public String AcessDeniedView(Model model) {
		logger.info("----ACCESS DENIED----");
		return "paginas/accessDenied";
	}
		
	@GetMapping("/inicio") 
	public String inicioVista(Model model) {
		try {
			return "paginas/main";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@PostMapping("/inicio") 
	public String inicioVistaPost(Model model, @RequestParam("messageAnio") String messageAnio) {
		try {
			model.addAttribute("success", messageAnio);
			return "paginas/main";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@PostMapping(value = "/login")
	public String login(LoginRequest loginRequest, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		
		String urlSistemaDefault = null;
		try {
			urlSistemaDefault = referenceService.getUrlSistemaDefault(SistemaDefault.CODTABLA, SistemaDefault.URLREDIRECTION.getCode());
			if(StringUtils.isBlank(urlSistemaDefault)) throw new Exception("No se encuentra las urls del sistema por defecto");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "paginas/error";
		}
		
		try {
			List<String> errors = loginValidator.validateLogin(loginRequest);
		    if(errors!=null && !errors.isEmpty()) {
	    		redirectAttributes.addFlashAttribute("info", errors.get(0));
		    	return "redirect:/loginadmform";
		    }
		    
		    /*
		     *  Método de autenticación
		     */
		    UserDetails userDetails = userService.autenticarUsuario(loginRequest.getUsuario().toUpperCase(), loginRequest.getClave(), String.valueOf(LocalDate.now().getYear()));
		    userDetails.setUrlLogin(urlSistemaDefault);
			Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            
			SecurityContext securityContext = SecurityContextHolder.getContext();
			securityContext.setAuthentication(authenticationToken);
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
						
		    return "redirect:/inicio";
		} catch (ValidacionPersonalizadaException e) {
			logger.error("ValidacionPersonalizadaException", e);
			redirectAttributes.addFlashAttribute("info", e.getMessage());
	    	return "redirect:/loginadmform";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}

	@GetMapping("/loginadmform") 
	public String loginVista(Model model) {		
		try {
			return "paginas/login";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);
			return "paginas/error";
		}
	}
	
	@GetMapping("/login") 
	public String login(@RequestParam(defaultValue = "") String tid, HttpServletRequest request, Model model) {
		logger.info("LOGIN REQUEST: " + request.getRequestURI()  +", tid: " + tid);
		String urlSistemaDefault = null;
		try {
			urlSistemaDefault = referenceService.getUrlSistemaDefault(SistemaDefault.CODTABLA, SistemaDefault.URLREDIRECTION.getCode());
			if(StringUtils.isBlank(urlSistemaDefault)) throw new Exception("No se encuentra las urls del sistema por defecto");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return "paginas/error";
		}
		
		
		try {
			String[] tokens = tid.split("\\*");
			if(tokens.length==2) {
				
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				String codUser = tokens[1];
				
				if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
					
					String ticket = tokens[0];
		        	String resultSession = referenceService.validateSession(Constantes.ID_SISTEMA, ticket, codUser);
					
		        	String[] datos = resultSession.split("\\*");
		        	String nombres = datos[0];
					String codUserBD = datos[1];
					String cempCodemp = datos[2];
					if(!codUser.equals(codUserBD)) throw new InvalidSession("Usuarios no coinciden " + codUser + " - " + codUserBD);
					
					List<GrantedAuthority> authorities = new ArrayList<>();
					UserDetails userDetails = new UserDetails(codUser, "", authorities);
					userDetails.setNombres(nombres);
					userDetails.setCodigoEmpleado(cempCodemp);
					userDetails.setAnio(String.valueOf(LocalDate.now().getYear()));
					userDetails.setCodinstitucion(Constantes.CODIGO_INSTITUCION);
					userDetails.setPeriodo(Constantes.PERIODO_EJECUCION);
					userDetails.setUrlLogin(urlSistemaDefault);
		            
					Authentication authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		            
					SecurityContext securityContext = SecurityContextHolder.getContext();
					securityContext.setAuthentication(authenticationToken);
		            HttpSession session = request.getSession(true);
		            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		        }
			}else {
				throw new LoginException("ticket no valido");
			}
			
			return "redirect:/inicio";
		} catch (LoginException e) {
			logger.error(e.getMessage());
			return "redirect:" + urlSistemaDefault + "/Aplicaciones.aspx";
		} catch (InvalidSession e) {
			logger.error(e.getMessage());
			return "redirect:" + urlSistemaDefault + "/Aplicaciones.aspx";
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			model.addAttribute("mensajeError", "Ocurrio un error al validar su acceso. Por favor, consultar con el administrador del sistema.");
			return "paginas/error";
		}
	}
	
	@GetMapping("/logout") 
	public String logout(Model model, HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
    		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    		logger.info("LOGOUT REQUEST: " + request.getRequestURI()  +", usuario: " + userDetails.getUsername());
    		
    		String urlSistemaDefault = userDetails.getUrlLogin();
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        	SecurityContextHolder.clearContext();
        	HttpSession session = request.getSession(true);
            session.removeAttribute("SPRING_SECURITY_CONTEXT");
            logger.info("Redireccionando a: " + urlSistemaDefault + "/Aplicaciones.aspx");
            return "redirect:" + urlSistemaDefault + "/Aplicaciones.aspx";
        }
        return "redirect:/inicio";
	}
}
