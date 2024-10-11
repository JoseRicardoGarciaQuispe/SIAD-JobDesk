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

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.repository.IHorasCompensablesRepository;
import gob.presidencia.siad.service.IMenuSemanalService;
import gob.presidencia.siad.util.Constantes;

import java.util.List;

/**
 * Controlador que maneja las solicitudes relacionadas con las Horas Compensables.
 * Utiliza las anotaciones de Spring MVC para gestionar las solicitudes HTTP.
 */
@Controller
@RequestMapping("/asistencia/horas-compensables")
public class HorasCompensablesController {
    
    // Logger para registrar mensajes y errores
    private static final Logger logger = LoggerFactory.getLogger(HorasCompensablesController.class);

    // Inyección de dependencia para el servicio de horas compensables
    @Autowired
    private IHorasCompensablesRepository horasCompensablesRepository;
    
    //Inyeccion de dependencia para el servicio de anios
    @Autowired
    private IMenuSemanalService menuSemanalService;

    /**
     * Método que maneja las solicitudes GET a la URL "/asistencia/horas-compensables".
     * Este método carga la vista principal donde se muestra las horas compensables.
     *
     * @param model Modelo para pasar datos a la vista.
     * @return La plantilla HTML correspondiente a la página principal de horascompensables.
     */
    @GetMapping
    public String inicioVista(Model model) {
        try {
            // Obtiene la información de autenticación del usuario actual desde el contexto de seguridad de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            List <Parametro> listaAnios = menuSemanalService.listarAnios();
            model.addAttribute("listaAnios", listaAnios);

            // Llama al servicio para obtener la lista de oficinas del usuario autenticado
            //El servicio llama al repositorio y este al SERVIDOR
            List<Parametro> oficinas = horasCompensablesRepository.listarOficinas(
                userDetails.getAnio(), 
                userDetails.getUsername(), 
                userDetails.getCodigoEmpleado());

            // Agrega un log para registrar los datos obtenidos desde el servidor
            logger.info("Datos obtenidos del servidor (oficinas): {}", oficinas);

            //El Servidor devuelve NOMBRE y CODIGO de 'oficinas'
            //Agrega el NOMBRE y CODIGO al modelo para que se muestre en la vista
            //Especificamente al selector identificado con el id ${listaOficinas}
            model.addAttribute("listaOficinas", horasCompensablesRepository.listarOficinas(
                userDetails.getAnio(), 
                userDetails.getUsername(), 
                userDetails.getCodigoEmpleado()) );

            // Retorna el nombre de la plantilla HTML correspondiente
            return "paginas/jobdesk/asistencia/horasCompensables/principal";
        } catch (Exception e) {
            // Registra el error en los logs
            logger.error("Error al cargar la vista de saldo vacacional", e);

            // Agrega un mensaje de error al modelo para mostrar en la vista
            model.addAttribute("mensajeError", Constantes.MESSAGE_ERROR_DEFAULT);

            // Redirige a la página de error
            return "paginas/error";
        }
    }   
}
