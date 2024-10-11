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
import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.ISaldoVacacionalService;
import gob.presidencia.siad.validator.SaldoVacacionalValidator;

/**
 * Controlador REST para manejar las solicitudes relacionadas con el saldo vacacional.
 * Este controlador devuelve datos en formato JSON como respuesta a las solicitudes AJAX.
 */
@RestController
@RequestMapping("/rest/asistencia/saldo-vacacional")  // Base URL para este controlador
public class SaldoVacacionalRestController {

    // Logger para registrar la actividad del controlador
    private static final Logger logger = LoggerFactory.getLogger(SaldoVacacionalRestController.class);

    // Inyección de dependencias del servicio de saldo vacacional
    @Autowired
    private ISaldoVacacionalService saldoVacacionalService;

    // Inyección de dependencias del validador de saldo vacacional
    @Autowired
    private SaldoVacacionalValidator saldoVacacionalValidator;

    /**
     * Maneja la solicitud POST para buscar los datos de saldo vacacional.
     * 
     * @param saldoVacacionalListadoRequest Objeto que contiene los filtros de búsqueda (enviado como JSON en la solicitud).
     * @return Un objeto ResponseEntity con los datos de saldo vacacional en formato JSON o un mensaje de error.
     */
    @PostMapping(value = "/buscar")
    public ResponseEntity<?> buscarSaldoVacacional(
        @RequestBody SaldoVacacionalListadoRequest saldoVacacionalListadoRequest) {
        try {
            // Validamos los datos de la solicitud con el validador personalizado
            List<String> errors = saldoVacacionalValidator.validateSearch(saldoVacacionalListadoRequest);
            if (errors != null && !errors.isEmpty()) {
                // Si hay errores de validación, devolvemos una respuesta con código HTTP 400 (Bad Request)
                return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
            }

            // Obtenemos los detalles del usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Establecemos los datos del usuario en el objeto de solicitud
            saldoVacacionalListadoRequest.setAnio(userDetails.getAnio());
            saldoVacacionalListadoRequest.setCodigoEmpleado(userDetails.getCodigoEmpleado());
            saldoVacacionalListadoRequest.setCodigoUsuario(userDetails.getUsername());

            // Calculamos el número de página para la paginación
            int pageNo = (saldoVacacionalListadoRequest.getStart() / saldoVacacionalListadoRequest.getLength()) + 1;

            // Llamamos al servicio para obtener los datos de saldo vacacional
            DataTable<SaldoVacacional> dataTable = saldoVacacionalService.listarSP(pageNo, saldoVacacionalListadoRequest.getLength(), saldoVacacionalListadoRequest);

            // Establecemos los parámetros de paginación en la respuesta
            dataTable.setDraw(saldoVacacionalListadoRequest.getDraw());
            dataTable.setStart(saldoVacacionalListadoRequest.getStart());

            // Devolvemos los datos en formato JSON con un código HTTP 200 (OK)
            return new ResponseEntity<>(dataTable, HttpStatus.OK);
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
