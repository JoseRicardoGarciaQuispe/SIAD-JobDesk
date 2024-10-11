package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.MenuSemanal;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Semana;
import gob.presidencia.siad.service.IMenuSemanalService;

@RestController
@RequestMapping("/rest/alimentacion/menu-semanal")
public class MenuSemanalRestController {

	private static final Logger logger = LoggerFactory.getLogger(MenuSemanalRestController.class);
	
	@Autowired
	private IMenuSemanalService menuSemanalService;
	
		
	@GetMapping(value = "/semanas/{anio}")
	public ResponseEntity<?> listarSemanas(@PathVariable("anio") String anio) {
		try {
			
			if (StringUtils.isBlank(anio)) throw new Exception("Error , el año es nulo o vacio");
			List<Semana> list = menuSemanalService.listarSemanas(anio);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("ERROR", e);
			List<String> errors = new ArrayList<>();
			errors.add(e.getMessage());
			return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value = "/buscar")
    public ResponseEntity<?> buscarMenuSemanal(@RequestBody String pk) {
	    try {
            if (StringUtils.isBlank(pk))
                throw new Exception("Error: el valor de PK es nulo o vacío");

            String[] tokens = pk.split("-");
            if (tokens.length < 2)
                throw new Exception("Error: PK no contiene alguno de los campos principales");

            String anio = tokens[0];
            String numSemana = tokens[1];
            	            
            List <Parametro> listaFechas = menuSemanalService.listarDias(anio, numSemana);

            // Se definen las constantes -> días de la semana
            String[] diasSemana = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

            // Lista de Objetos Map con los nombres de los días y las fechas de obtenidas
            List<Map<String, String>> diasFechas = new ArrayList<>();
            for (int i = 0; i < listaFechas.size(); i++) {
                Parametro fecha = listaFechas.get(i);
                String nombreDia = diasSemana[i];
                
                // Relacionando nombre del día con la fecha
                Map<String, String> diaFechaMap = new HashMap<>();
                diaFechaMap.put("nombreDia", nombreDia);
                diaFechaMap.put("fecha", fecha.getNombre());
                diasFechas.add(diaFechaMap);
            }
            return new ResponseEntity<>(diasFechas, HttpStatus.OK);    
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
	 
	 
	@PostMapping(value = "/listar")
	public ResponseEntity<?> listarMenuSemanal(@RequestBody String pk) {
	     try {
	         if (StringUtils.isBlank(pk))
	             throw new Exception("Error: el valor de PK es nulo o vacío");

	         String[] tokens = pk.split("-");
	         if (tokens.length < 3)
	             throw new Exception("Error: PK no contiene alguno de los campos principales");

	         String anio = tokens[0];
	         String numSemana = tokens[1];
	         String fecha = tokens[2];

	         List<MenuSemanal> listarMenuSemanal = menuSemanalService.listarMenuSemanal(anio, numSemana, fecha);
	         	         
	         Map<String, List<MenuSemanal>> platosPorTipo = new HashMap<>();
	         for (MenuSemanal menu : listarMenuSemanal) {
	             String tipo = menu.getChorcodtipo();
	             List<MenuSemanal> platos = platosPorTipo.getOrDefault(tipo, new ArrayList<>());
	             platos.add(menu);
	             platosPorTipo.put(tipo, platos);
	         }
	         
	         return new ResponseEntity<>(platosPorTipo, HttpStatus.OK);	   
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