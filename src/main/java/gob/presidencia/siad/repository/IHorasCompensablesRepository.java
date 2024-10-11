package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.HorasCompensables;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.HorasCompensablesListadoRequest;

/**
 * Interfaz del repositorio para manejar las operaciones relacionadas con las Horas Compensables.
 * Define los métodos que interactúan con la base de datos para realizar consultas específicas.
 */
public interface IHorasCompensablesRepository {

    /*
     * Método para listar las oficinas disponibles para un usuario autenticado.
     *
     * @param anio El año correspondiente.
     * @param codigoUsuario El código del usuario autenticado.
     * @param codigoEmpleado El código del empleado asociado al usuario.
     * @return Una lista de parámetros que contiene las oficinas.
     * @throws Exception En caso de error al realizar la consulta.
     */
    List<Parametro> listarOficinas(
        String anio, 
        String codigoUsuario, 
        String codigoEmpleado) throws Exception;

    /*
     * Método para listar las horas compensables paginadas y filtradas.
     *
     * @param anio El año seleccionado.
     * @param mes El mes seleccionado.
     * @param codigoEmpleado El código del empleado para la búsqueda.
     * @param horasCompensablesListadoRequest El objeto que contiene los filtros adicionales para la búsqueda.
     * @return Un objeto DataTable que contiene las horas compensables y la información de paginación.
     * @throws Exception En caso de error al realizar la consulta.
     */
    DataTable<HorasCompensables> listarHorasCompensables(
        int pageNo,
        int pageSize,
        HorasCompensablesListadoRequest horasCompensablesRequest) throws Exception;
}
