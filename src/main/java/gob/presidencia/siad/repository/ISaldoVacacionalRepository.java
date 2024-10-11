package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;

/**
 * Interfaz del repositorio para manejar las operaciones relacionadas con el saldo vacacional.
 * Esta interfaz define los métodos que interactúan con la base de datos para realizar consultas específicas.
 */
public interface ISaldoVacacionalRepository {
    
    /**
     * Método para listar las oficinas disponibles para un usuario dado.
     * Este método probablemente ejecuta un procedimiento almacenado (SP) en la base de datos.
     *
     * @param anio El año correspondiente.
     * @param codigoUsuario El código del usuario autenticado.
     * @param codigoEmpleado El código del empleado asociado al usuario.
     * @return Una lista de objetos Parametro que contiene las oficinas disponibles.
     * @throws Exception En caso de error al realizar la consulta a la base de datos.
     */
    public List<Parametro> listarOficinasSP(String anio, String codigoUsuario, String codigoEmpleado) throws Exception;

    /**
     * Método para listar los saldos vacacionales con paginación.
     * Ejecuta una consulta paginada que devuelve los resultados filtrados por los parámetros proporcionados.
     *
     * @param pageNo El número de página para la paginación.
     * @param pageSize El tamaño de página (número de registros por página).
     * @param saldoVacacionalRequest El objeto que contiene los filtros de búsqueda como la oficina y empleado.
     * @return Un objeto DataTable que contiene los saldos vacacionales y la información de paginación.
     * @throws Exception En caso de error al realizar la consulta a la base de datos.
     */
    public DataTable<SaldoVacacional> listarSP(int pageNo, int pageSize, SaldoVacacionalListadoRequest saldoVacacionalRequest) throws Exception;
}
