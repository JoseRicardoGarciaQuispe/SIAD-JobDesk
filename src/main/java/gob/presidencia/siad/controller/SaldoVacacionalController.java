package gob.presidencia.siad.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.service.ISaldoVacacionalService;
import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;
import gob.presidencia.siad.util.Constantes;

import java.util.List;
import java.util.Collections;

/**
 * Controlador que maneja las solicitudes relacionadas con el saldo vacacional.
 * Utiliza las anotaciones de Spring MVC para gestionar las solicitudes HTTP.
 */
@Controller
@RequestMapping("/asistencia/saldo-vacacional")
public class SaldoVacacionalController {
    
    // Logger para registrar mensajes y errores
    private static final Logger logger = LoggerFactory.getLogger(SaldoVacacionalController.class);

    // Inyección de dependencia para el servicio de saldo vacacional
    @Autowired
    private ISaldoVacacionalService saldoVacacionalService;

    /**
     * Método que maneja las solicitudes GET a la URL "/asistencia/saldo-vacacional".
     * Este método carga la vista principal donde se muestra el saldo vacacional.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return La plantilla HTML correspondiente a la página principal de saldo vacacional.
     */
    @GetMapping
    public String inicioVista(Model model) {
        try {
            // Obtiene la información de autenticación del usuario actual desde el contexto de seguridad de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Llama al servicio para obtener la lista de oficinas del usuario autenticado
            List<Parametro> oficinas = saldoVacacionalService.listarOficinas(
                userDetails.getAnio(), 
                userDetails.getUsername(), 
                userDetails.getCodigoEmpleado());

            // Agrega un log para registrar los datos obtenidos desde el servidor
            logger.info("Datos obtenidos del servidor (oficinas): {}", oficinas);

            //El Servidor devuelve NOMBRE y CODIGO de 'oficinas'
            //Agrega el NOMBRE y CODIGO al modelo para que se muestre en la vista
            //Especificamente al selector identificado con el id ${listaOficinas}
            model.addAttribute("listaOficinas", oficinas);

            // Retorna el nombre de la plantilla HTML correspondiente
            return "paginas/jobdesk/asistencia/saldoVacacional/principal";
        } catch (Exception e) {
            // Registra el error en los logs
            logger.error("Error al cargar la vista de saldo vacacional", e);

            // Agrega un mensaje de error al modelo para mostrar en la vista
            model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);

            // Redirige a la página de error
            return "paginas/error";
        }
    }

    /**
     * Método para buscar el saldo vacacional de los empleados. Responde a solicitudes AJAX.
     * El método realiza la búsqueda y devuelve los datos en formato JSON.
     *
     * @param pageNo Número de página para la paginación.
     * @param pageSize Tamaño de página (número de registros por página).
     * @param anio Año seleccionado.
     * @param empleado Código del empleado para filtrar la búsqueda.
     * @return Lista de saldos vacacionales en formato JSON.
     */
   @GetMapping("/buscar")
@ResponseBody
public List<SaldoVacacional> buscarSaldoVacacional(
    int pageNo, 
    int pageSize, 
    String anio, 
    String empleado) {
    try {
        // Crear objeto de tipo SaldoVacacionalListadoRequest para pasar al servicio
        SaldoVacacionalListadoRequest request = new SaldoVacacionalListadoRequest();
        request.setAnio(anio);
        request.setEmpleado(empleado);
        request.setStart((pageNo - 1) * pageSize);  // Calcular el inicio para la paginación
        request.setLength(pageSize);

        // Llamar al servicio que devuelve un DataTable<SaldoVacacional>
        DataTable<SaldoVacacional> dataTable = saldoVacacionalService.listarSP(
            pageNo, 
            pageSize, 
            request);

        // Extraer la lista de SaldoVacacional desde el DataTable
        return dataTable.getData();
    } catch (Exception e) {
        logger.error("Error al buscar el saldo vacacional", e);
        return Collections.emptyList();  // En caso de error, devolver una lista vacía
    }
}

}
