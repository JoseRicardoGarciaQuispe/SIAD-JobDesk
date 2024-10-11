package gob.presidencia.siad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;
import gob.presidencia.siad.repository.ISaldoVacacionalRepository;
import gob.presidencia.siad.service.ISaldoVacacionalService;

/**
 * Implementación del servicio de saldo vacacional que maneja la lógica de negocio.
 * Se comunica con el repositorio para acceder a los datos de la base de datos.
 */
@Service
public class SaldoVacacionalServiceImpl implements ISaldoVacacionalService {
    
    // Inyección de dependencia del repositorio de saldo vacacional
    @Autowired
    private ISaldoVacacionalRepository saldoVacacionalRepository;

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
        return saldoVacacionalRepository.listarOficinasSP(
            anio, 
            codigoUsuario, 
            codigoEmpleado);
    }

    /**
     * Método para listar los saldos vacacionales paginados.
     * Este método llama al repositorio para obtener los datos paginados desde la base de datos.
     *
     * @param pageNo El número de página para la paginación.
     * @param pageSize El tamaño de página (número de registros por página).
     * @param saldoVacacionalRequest El objeto que contiene los filtros para la búsqueda (oficina, empleado, etc.).
     * @return Un objeto DataTable que contiene los saldos vacacionales y la información de paginación.
     * @throws Exception En caso de error al realizar la consulta.
     */
    @Override
    @Transactional(readOnly = true)  // Indica que esta transacción es de solo lectura
    public DataTable<SaldoVacacional> listarSP(
        int pageNo, 
        int pageSize, 
        SaldoVacacionalListadoRequest saldoVacacionalRequest) throws Exception {
        
        // Llama al repositorio para obtener los saldos vacacionales paginados desde la base de datos
        return saldoVacacionalRepository.listarSP(
            pageNo, 
            pageSize, 
            saldoVacacionalRequest);
    }
}
