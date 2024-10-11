package gob.presidencia.siad.restcontroller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.FileDocument;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Reporte;
import gob.presidencia.siad.model.request.ExportarDataListadoRequest;
import gob.presidencia.siad.model.request.GenerarReporteRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IExportarDataService;

@RestController
@RequestMapping("/rest/exportar-data")
public class ExportarDataRestController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExportarDataRestController.class);
	
	@Autowired
	private IExportarDataService exportarDataService;
		
	@PostMapping(value = "/buscar")
	public ResponseEntity<?> buscarReportes(@RequestBody ExportarDataListadoRequest request) {
		try {		    
		    int pageNo = (request.getStart() / request.getLength()) + 1;
		    
		    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		    request.setUsuario(userDetails.getUsername());
		    
			DataTable<Reporte> dataTable = exportarDataService.listarReportes(pageNo, request.getLength(), request);

		    dataTable.setDraw(request.getDraw());
		    dataTable.setStart(request.getStart());
		    
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
	
	@PostMapping(value = "/reporte/generar")
	public ResponseEntity<?> generarReporte(@RequestBody GenerarReporteRequest request) {    
		try {		
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		    
			byte[] data = exportarDataService.generarListadoReporte(request, userDetails.getNombres());
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
			Date now = new Date();
			String filename = "Reporte " + simpleDateFormat.format(now) + ".xlsx";
			if(data != null && data.length>0) {
				FileDocument fileDocument = new FileDocument();
				fileDocument.setArchivo(data);
				fileDocument.setNombre(filename);
				return new ResponseEntity<>(fileDocument, HttpStatus.OK);
			}else {
				List<String> errors = new ArrayList<>();
				errors.add("No se encontraron registros para exportar");
				return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
			}
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
	
	@GetMapping(value = "/reporte/buscar/grupos")
	public ResponseEntity<?> buscarGrupos(@RequestParam(value = "termino", required = true, defaultValue = "") String termino) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> list = exportarDataService.buscarGrupos(userDetails.getAnio(), termino);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/reporte/buscar/articulos")
	public ResponseEntity<?> buscarArticulos(@RequestParam(value = "termino", required = true, defaultValue = "") String termino) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> list = exportarDataService.buscarArticulos(userDetails.getAnio(), termino);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/reporte/buscar/clases")
	public ResponseEntity<?> buscarClases(@RequestParam(value = "termino", required = true, defaultValue = "") String termino,
			@RequestParam(value = "codgru", required = true) String codgru) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> list = exportarDataService.buscarClases(userDetails.getAnio(), codgru, termino);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/reporte/listar/oficinas")
	public ResponseEntity<?> listarOficinas() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			List<Parametro> list = exportarDataService.listarOficinas(userDetails.getAnio(), userDetails.getUsername());
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/reporte/listar/anios")
	public ResponseEntity<?> listarAnios() {
		try {
			List<Parametro> list = exportarDataService.listarAnios();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
