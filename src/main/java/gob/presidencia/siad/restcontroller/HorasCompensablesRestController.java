package gob.presidencia.siad.restcontroller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.HorasCompensables;
import gob.presidencia.siad.model.request.HorasCompensablesListadoRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.IHorasCompensablesService;
import gob.presidencia.siad.validator.HorasCompensablesValidator;

/**
 * Controlador REST que maneja las solicitudes relacionadas con la consulta de Horas Compensables.
 * Este controlador devuelve datos en formato JSON como respuesta a las solicitudes AJAX.
 */
@RestController // @RestController indica que este controlador manejará solicitudes Ajax y devolverá JSON
@RequestMapping("/rest/asistencia/horas-compensables") // Base URL para este controlador
public class HorasCompensablesRestController {

    // Logger para registrar la actividad del controlador
    private static final Logger logger = LoggerFactory.getLogger(HorasCompensablesRestController.class);

    // Inyección de dependencia para el servicio de horas compensables
    @Autowired
    private IHorasCompensablesService horasCompensablesService;

    // Inyección de dependencias del validador de horas compensables
    @Autowired
    private HorasCompensablesValidator horasCompensablesValidator;

    /**
     * Maneja las solicitudes POST para buscar las horas compensables basadas en los filtros proporcionados.
     *
     * @param horasCompensablesListadoRequest Objeto que contiene los filtros de búsqueda.
     * @return Una tabla de datos con las horas compensables filtradas.
     */
    @PostMapping("/buscar")
    public ResponseEntity<?> buscarHorasCompensables(@RequestBody HorasCompensablesListadoRequest horasCompensablesRequest) {
        try {
            // Obtenemos los detalles del usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Establecemos el código de empleado desde el Spring Security
            horasCompensablesRequest.setAnio(userDetails.getAnio());
            horasCompensablesRequest.setCodigoEmpleado(userDetails.getCodigoEmpleado());
            horasCompensablesRequest.setCodigoUsuario(userDetails.getUsername());

            // Validamos los datos de la solicitud con el validator personalizado
            List<String> errors = horasCompensablesValidator.validateSearch(horasCompensablesRequest);
            if (errors != null && !errors.isEmpty()) {
                // Si hay errores de validación, devolvemos una respuesta con código HTTP 400 (Bad Request)
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            int pageNo = (horasCompensablesRequest.getStart() /horasCompensablesRequest.getLength()) + 1;
            DataTable<HorasCompensables> datatable = horasCompensablesService.listarHorasCompensables(
                pageNo, 
                horasCompensablesRequest.getLength(), 
                horasCompensablesRequest);

           




            // Establecemos los parámetros de paginación en la respuesta
            datatable.setDraw(horasCompensablesRequest.getDraw());
            datatable.setStart(horasCompensablesRequest.getStart());

            // Devolvemos los datos en formato JSON con un código HTTP 200 (OK)
            return new ResponseEntity<>(datatable, HttpStatus.OK);
        } catch (ValidacionPersonalizadaException e) {
            // Si ocurre una excepción personalizada de validación, registramos el error y devolvemos una respuesta con código 400
            logger.info("ValidacionPersonalizadaException: " + e.getMessage());
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // En caso de una excepción general, registramos el error y devolvemos una respuesta con código 500 (Internal Server Error)
            logger.error("ERROR", e);
            List<String> errors = new ArrayList<>();
            errors.add(e.getMessage());
            return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
