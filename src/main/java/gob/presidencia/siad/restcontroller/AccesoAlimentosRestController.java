package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.AccesoAlimentos;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;
import gob.presidencia.siad.service.IAccesoAlimentosService;
import gob.presidencia.siad.validator.AccesoAlimentosValidator;

@RestController
@RequestMapping("/rest/alimentacion/acceso-alimentos")
public class AccesoAlimentosRestController {
private static final Logger logger = LoggerFactory.getLogger(AccesoAlimentosRestController.class);
	
	@Autowired
	private IAccesoAlimentosService accesoAlimentosService;
	
	@Autowired
	private AccesoAlimentosValidator accesoAlimentosValidator;
	
	@PostMapping(value = "/buscar/paginado")
	public ResponseEntity<?> buscarAccesoAlimentos(@RequestBody AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) {
		try {
			List<String> errors = accesoAlimentosValidator.validateSearch(accesoAlimentosListadoRequest);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
		    int pageNo = (accesoAlimentosListadoRequest.getStart() / accesoAlimentosListadoRequest.getLength()) + 1;
			DataTable<AccesoAlimentos> dataTable = accesoAlimentosService.listarSP(pageNo, accesoAlimentosListadoRequest.getLength(), accesoAlimentosListadoRequest);

		    dataTable.setDraw(accesoAlimentosListadoRequest.getDraw());
		    dataTable.setStart(accesoAlimentosListadoRequest.getStart());
		    
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

	@PostMapping(value = "/buscar")
	public ResponseEntity<?> buscarAccesoAlimentos2(@RequestBody AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) {
		try {
			List<String> errors = accesoAlimentosValidator.validateSearch(accesoAlimentosListadoRequest);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
			List<AccesoAlimentos> data = accesoAlimentosService.listarAllSP(accesoAlimentosListadoRequest);

			return new ResponseEntity<>(data, HttpStatus.OK);
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
}
