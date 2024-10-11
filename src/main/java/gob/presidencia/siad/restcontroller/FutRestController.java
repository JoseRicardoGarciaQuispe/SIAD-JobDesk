package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.exception.FutException;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.request.FutListadoRequest;
import gob.presidencia.siad.service.IFutService;
import gob.presidencia.siad.validator.FutValidator;

@RestController
@RequestMapping("/rest/asistencia/fut")
public class FutRestController {

	private static final Logger logger = LoggerFactory.getLogger(FutRestController.class);
	
	@Autowired
	private IFutService futService;
	
	@Autowired
	private FutValidator futValidator;
	
	@PostMapping(value = "/buscar")
	public ResponseEntity<?> buscarFut(@RequestBody FutListadoRequest futListadoRequest) {
		
		logger.info("Ingresando al método BuscarFUT");
		
		int pageNo = (futListadoRequest.getStart() / futListadoRequest.getLength()) + 1;
		DataTable<Fut> dataTable = new DataTable<Fut>();
		long total = 0;
		try {
			List<Fut> lista = new ArrayList<>();
			
			logger.info("listando los FUT's");
    		
			total = futService.totalListar(futListadoRequest);
			lista = futService.listar(pageNo, futListadoRequest.getLength(), futListadoRequest);
			
			
			logger.info("Se listan los Datatable en la tabla FUT");
		    dataTable.setData(lista);
		    dataTable.setRecordsTotal(total);
		    dataTable.setRecordsFiltered(total);

		    dataTable.setDraw(futListadoRequest.getDraw());
		    dataTable.setStart(futListadoRequest.getStart());
		    
			return new ResponseEntity<>(dataTable, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value = "/registrar")
	public ResponseEntity<?> registrarFUT(@RequestBody Fut fut) {
		
		try {
			List<String> errors = futValidator.validateCreate(fut);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    			
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			fut.setCusuUsureg(username);
			
			futService.insertar(fut);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FutException e) {
			logger.error("FutException: Validacion personalizada", e);
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
	public ResponseEntity<?> editarFut(@RequestBody Fut fut) {
		
		try {
			List<String> errors = futValidator.validateCreate(fut);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    			
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			fut.setCusuUsureg(username);
						
			futService.actualizar(fut);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FutException e) {
			logger.error("FutException: Validacion personalizada", e);
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
	public ResponseEntity<?> aprobarFut(@RequestBody Fut fut) {
		
		try {
			List<String> errors = futValidator.validatePK(fut);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			fut.setCusuUsureg(username);
			
			futService.aprobar(fut);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FutException e) {
			logger.error("FutException: Validacion personalizada", e);
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
	public ResponseEntity<?> anularFut(@RequestBody Fut fut) {
		
		try {
			List<String> errors = futValidator.validatePK(fut);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			fut.setCusuUsureg(username);
			
			futService.anular(fut);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (FutException e) {
			logger.error("FutException: Validacion personalizada", e);
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
}
