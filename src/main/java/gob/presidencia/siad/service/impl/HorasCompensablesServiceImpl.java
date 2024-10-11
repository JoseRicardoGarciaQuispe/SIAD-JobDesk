package gob.presidencia.siad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.HorasCompensables;
import gob.presidencia.siad.model.request.HorasCompensablesListadoRequest;
import gob.presidencia.siad.repository.IHorasCompensablesRepository;
import gob.presidencia.siad.service.IHorasCompensablesService;

/**
 * Implementación del servicio para manejar las operaciones relacionadas con las Horas Compensables.
 * Esta clase implementa la lógica de negocio definida en la interfaz IHorasCompensablesService.
 * Se comunica con el repositorio para acceder a los datos de la base de datos.
 */
@Service
public class HorasCompensablesServiceImpl implements IHorasCompensablesService {
    
    // Inyección de dependencia del repositorio de horas compensables
    @Autowired
    private IHorasCompensablesRepository horasCompensablesRepository;

    /**
     * Método para listar las oficinas disponibles para un usuario autenticado.
     * Este método llama al repositorio que interactúa con la base de datos.
     *
     * @param anio El año correspondiente.
     * @param codigoUsuario El código del usuario autenticado.
     * @param codigoEmpleado El código del empleado asociado al usuario.
     * @return Una lista de parámetros que contiene las oficinas.
     * @throws Exception En caso de error al realizar la consulta.
     */
    @Override
    @Transactional(readOnly = true)  // Indica que esta transacción es de solo lectura (no modifica datos)
    public List<Parametro> listarOficinas(
        String anio, 
        String codigoUsuario, 
        String codigoEmpleado) throws Exception {
        // Llama al repositorio para obtener la lista de oficinas desde la base de datos
        return horasCompensablesRepository.listarOficinas(
            anio, 
            codigoUsuario, 
            codigoEmpleado);
    }

    /**
     * Método para listar las horas compensables paginadas y filtradas por anio, mes y código de empleado.
     * Este método llama al repositorio para obtener los datos filtrados y paginados desde la base de datos.
     *
     * @param pageNo El número de página para la paginación.
     * @param mes El mes seleccionado.
     * @param codigoEmpleado El código del empleado para la búsqueda.
     * @param horasCompensablesListadoRequest El objeto que contiene los filtros adicionales para la búsqueda.
     * @return Un objeto DataTable que contiene las horas compensables y la información de paginación.
     * @throws Exception En caso de error al realizar la consulta.
     */
    @Override
    @Transactional(readOnly = true)  // Indica que esta transacción es de solo lectura
    public DataTable<HorasCompensables> listarHorasCompensables(
        int pageNo,
        int pageSize, 
        HorasCompensablesListadoRequest horasCompensablesRequest) throws Exception {
        // Llama al repositorio para obtener las horas compensables paginadas desde la base de datos con los nuevos filtros
        return horasCompensablesRepository.listarHorasCompensables(
            pageNo,
            pageSize,            
            horasCompensablesRequest);
    }
}
