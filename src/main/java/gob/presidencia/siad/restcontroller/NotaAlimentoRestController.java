package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.request.NotaAlimentoListadoRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.INotasAlimentosService;
import gob.presidencia.siad.validator.NotaAlimentoValidator;

@RestController
@RequestMapping("/rest/alimentacion/notas-alimentos")
public class NotaAlimentoRestController {

	private static final Logger logger = LoggerFactory.getLogger(NotaAlimentoRestController.class);
	
	@Autowired
	private INotasAlimentosService notasAlimentosService;
	
	@Autowired
	private NotaAlimentoValidator notaAlimentoValidator;
	
	@PostMapping(value = "/buscar")
	public ResponseEntity<?> buscarNotaAlimentos(@RequestBody NotaAlimentoListadoRequest notaAlimentoRequest) {
		
		int pageNo = (notaAlimentoRequest.getStart() / notaAlimentoRequest.getLength()) + 1;
		DataTable<NotaAlimento> dataTable = new DataTable<NotaAlimento>();
		long total = 0;
		try {
			List<NotaAlimento> lista = new ArrayList<>();
    		
			total = notasAlimentosService.totalListar(notaAlimentoRequest);
			lista = notasAlimentosService.listar(pageNo, notaAlimentoRequest.getLength(), notaAlimentoRequest);
			
		    dataTable.setData(lista);
		    dataTable.setRecordsTotal(total);
		    dataTable.setRecordsFiltered(total);

		    dataTable.setDraw(notaAlimentoRequest.getDraw());
		    dataTable.setStart(notaAlimentoRequest.getStart());
		    
			return new ResponseEntity<>(dataTable, HttpStatus.OK);
		} catch (ValidacionPersonalizadaException e) {
			logger.info("ValidacionPersonalizadaException: " + e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/registrar")
	public ResponseEntity<?> registrarNotaAlimentos(@RequestBody NotaAlimento notaAlimento) {
		
		try {
			List<String> errors = notaAlimentoValidator.validateCreate(notaAlimento);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    			
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		    notaAlimento.setCusuUsureg(userDetails.getUsername());
		    notaAlimento.setCanoAnosol(userDetails.getAnio());
			
			notasAlimentosService.insertar(notaAlimento);
			
			return new ResponseEntity<>(notaAlimento, HttpStatus.OK);
		} catch (ValidacionPersonalizadaException e) {
			logger.info("ValidacionPersonalizadaException: " + e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/editar")
	public ResponseEntity<?> editarNotaAlimentos(@RequestBody NotaAlimento notaAlimento) {
		
		try {
			List<String> errors = notaAlimentoValidator.validateUpdate(notaAlimento);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    			
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		    notaAlimento.setCusuUsureg(userDetails.getUsername());
			notaAlimento.setCusuUsumod(userDetails.getUsername());
			
			notasAlimentosService.actualizar(notaAlimento);
			
			return new ResponseEntity<>(notaAlimento, HttpStatus.OK);
		} catch (ValidacionPersonalizadaException e) {
			logger.info("ValidacionPersonalizadaException: " + e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/aprobar")
	public ResponseEntity<?> aprobarNotaAlimentos(@RequestBody NotaAlimento notaAlimento) {
		
		try {
			List<String> errors = notaAlimentoValidator.validatePK(notaAlimento);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			notaAlimento.setCusuUsumod(userDetails.getUsername());
			
			notasAlimentosService.aprobar(notaAlimento);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ValidacionPersonalizadaException e) {
			logger.info("ValidacionPersonalizadaException: " + e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping(value = "/anular")
	public ResponseEntity<?> anularNotaAlimentos(@RequestBody NotaAlimento notaAlimento) {
		
		try {
			List<String> errors = notaAlimentoValidator.validatePK(notaAlimento);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			notaAlimento.setCusuUsumod(userDetails.getUsername());
			
			notasAlimentosService.anular(notaAlimento);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (ValidacionPersonalizadaException e) {
			logger.info("ValidacionPersonalizadaException: " + e.getMessage());
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/empleados/{codigoOficina}/{indicadorApoyo}")
	public ResponseEntity<?> buscarEmpleadosPorOficina(@PathVariable("codigoOficina") String codigoOficina, @PathVariable("indicadorApoyo") String indicadorApoyo) {
		try {
			if (StringUtils.isBlank(codigoOficina)) throw new Exception("Error codigo de oficina es nulo o vacio");
			if (StringUtils.isBlank(indicadorApoyo)) throw new Exception("Error indicador de apoyo nulo o vacio");
			
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			
			List<Empleado> list = notasAlimentosService.listarEmpleadosPorOficinaSP(userDetails.getAnio(), userDetails.getUsername(), codigoOficina, indicadorApoyo);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
