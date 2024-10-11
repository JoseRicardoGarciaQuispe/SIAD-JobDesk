
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
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Tardanzas;
import gob.presidencia.siad.model.request.TardanzasListadoRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.ITardanzasService;
import gob.presidencia.siad.validator.TardanzasValidator;

@RestController
@RequestMapping("/rest/asistencia/tardanzas")
public class TardanzasRestController {

	private static final Logger logger = LoggerFactory.getLogger(TardanzasRestController.class);
	
	@Autowired
	private ITardanzasService tardanzasService;
	
	@Autowired
	private TardanzasValidator tardanzasValidator;
	
	@PostMapping(value = "/buscar")
	public ResponseEntity<?> buscarTardanzas(@RequestBody TardanzasListadoRequest tardanzasRequest) {
		try {
			List<String> errors = tardanzasValidator.validateSearch(tardanzasRequest);
		    if(errors!=null && !errors.isEmpty()) return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
		    
		    int pageNo = (tardanzasRequest.getStart() / tardanzasRequest.getLength()) + 1;
			DataTable<Tardanzas> dataTable = new DataTable<Tardanzas>();
			long total = 0;
		    
			List<Tardanzas> lista = new ArrayList<>();
    		
			total = tardanzasService.totalListar(tardanzasRequest);
			lista = tardanzasService.listar(pageNo, tardanzasRequest.getLength(), tardanzasRequest);
			
		    dataTable.setData(lista);
		    dataTable.setRecordsTotal(total);
		    dataTable.setRecordsFiltered(total);

		    dataTable.setDraw(tardanzasRequest.getDraw());
		    dataTable.setStart(tardanzasRequest.getStart());
		    
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
	
	@GetMapping(value = "/empleados/{codigoOficina}")
	public ResponseEntity<?> buscarTardanzas(@PathVariable("codigoOficina") String codigoOficina) {
		try {
			if (StringUtils.isBlank(codigoOficina)) throw new Exception("Error codigo de oficina es nulo o vacio");
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> list = tardanzasService.listarEmpleadosPorOficina(userDetails.getAnio(), userDetails.getUsername(), codigoOficina);
			return new ResponseEntity<>(list, HttpStatus.OK);
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
