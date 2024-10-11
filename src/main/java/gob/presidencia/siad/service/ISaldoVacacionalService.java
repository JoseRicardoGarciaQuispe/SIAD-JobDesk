package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;

/**
 * Interfaz del repositorio para manejar las operaciones relacionadas con el saldo vacacional.
 * Define los métodos que interactúan con la base de datos para realizar consultas específicas.
 */
public interface ISaldoVacacionalService {
    
    /**
     * Método para listar las oficinas disponibles para un usuario dado.
     *
     * @param anio El año correspondiente.
     * @param codigoUsuario El código del usuario autenticado.
     * @param codigoEmpleado El código del empleado asociado al usuario.
     * @return Una lista de parámetros que contiene las oficinas.
     * @throws Exception En caso de error al realizar la consulta.
     */
    public List<Parametro> listarOficinas(
        String anio, 
        String codigoUsuario, 
        String codigoEmpleado) throws Exception;

    /**
     * Método para listar los saldos vacacionales paginados.
     *
     * @param pageNo El número de página para la paginación.
     * @param pageSize El tamaño de página, es decir, cuántos registros se mostrarán por página.
     * @param saldoVacacionalRequest El objeto que contiene los filtros para la búsqueda (oficina, empleado, etc.).
     * @return Un objeto DataTable que contiene los saldos vacacionales y la información de paginación.
     * @throws Exception En caso de error al realizar la consulta.
     */
    public DataTable<SaldoVacacional> listarSP(
        int pageNo, 
        int pageSize, 
        SaldoVacacionalListadoRequest saldoVacacionalRequest) throws Exception;
}
