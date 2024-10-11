package gob.presidencia.siad.repository.impl;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.HorasCompensables;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.HorasCompensablesListadoRequest;
import gob.presidencia.siad.repository.IHorasCompensablesRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class HorasCompensablesRepositoryImpl implements IHorasCompensablesRepository {
    
    // Logger para registrar información y errores
    private static final Logger logger = LoggerFactory.getLogger(HorasCompensablesRepositoryImpl.class);

    // Inyección de dependencia de JdbcTemplate para interactuar con la base de datos
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Inyección de dependencia de RepositoryUtil para manejar errores de la BD
    @Autowired
    private RepositoryUtil repositoryUtil;
    
    /**
     * Implementación del método para listar las oficinas disponibles.
     * Se basa en una llamada a un procedimiento almacenado.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Parametro> listarOficinas(String anio, String codigoUsuario, String codigoEmpleado) throws Exception {
        try {
            // Configuración de la llamada al procedimiento almacenado
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_OFI_VACA") 
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                        new SqlParameter("p_vV_ANIO", Types.VARCHAR),
                        new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
                        new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
                        new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
                        new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
                        new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
                    );

            // Parámetros de entrada para el procedimiento almacenado
            SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("p_vV_ANIO", anio)
                .addValue("p_vV_CODUSU", codigoUsuario)
                .addValue("p_vV_CEMP_CODEMP", codigoEmpleado);

            // Ejecución del procedimiento almacenado
            Map<String, Object> result = jdbcCall.execute(inParams);
            
            // Verificación del resultado de la consulta
            repositoryUtil.analizarResultado(result);
            
            if (result.get("p_cLISTADO") == null) throw new Exception("Respuesta esperada nula");

            // Retorno de la lista de oficinas
            return (List<Parametro>) result.get("p_cLISTADO");
        } catch (ValidacionPersonalizadaException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("Error al listar oficinas");
        }
    }
    
    /**
     * Implementación del método para listar las horas compensables paginadas.
     * Utiliza un procedimiento almacenado.
     */
    @SuppressWarnings("unchecked")
    @Override
    public DataTable<HorasCompensables> listarHorasCompensables(
       int pageNo,
       int pageSize,
        HorasCompensablesListadoRequest horasCompensablesRequest) throws Exception {
        try {
            int offset = (pageNo - 1) * pageSize;
            // Configuración de la llamada al procedimiento almacenado
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_HORAS_COMP")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                        new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),//codigo empleado
                        new SqlParameter("p_vV_ANIO", Types.VARCHAR), //ano
                        new SqlParameter("p_vV_MES", Types.VARCHAR),//mes
                        new SqlParameter("p_vV_OFFSET", Types.VARCHAR),
                        new SqlParameter("p_vV_SIZE", Types.VARCHAR),                        
                        new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER), // flag resultado
                        new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
                        new SqlOutParameter("p_nTOTAL_RESULTADO", Types.INTEGER), // mensaje resultado
                        new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) // cursor
                    );

            // Parámetros de entrada para el procedimiento almacenado
            SqlParameterSource inParams = new MapSqlParameterSource()
                .addValue("p_vV_CEMP_CODEMP", horasCompensablesRequest.getCodigoEmpleado())
                .addValue("p_vV_ANIO", horasCompensablesRequest.getAnio())
                .addValue("p_vV_MES", horasCompensablesRequest.getMes())
                .addValue("p_vV_OFFSET", offset)
                .addValue("p_vV_SIZE", pageSize)
                ;

            // Ejecución del procedimiento almacenado
            Map<String, Object> result = jdbcCall.execute(inParams);

            // Verificación del resultado del flag y el mensaje de salida
            Integer flagResultado = (Integer) result.get("p_nFLAG_RESULTADO");
            String mensajeResultado = (String) result.get("p_vMENSAJE_RESULTADO");

             // Registrar los resultados en el log
             logger.info("Flag Resultado: " + flagResultado);
             logger.info("Mensaje Resultado: " + mensajeResultado);
 
             // Validar el resultado y manejar el error si el flag indica un problema
             repositoryUtil.analizarResultado(result);

             // Manejar el cursor de salida
            if (result.get("p_cLISTADO") == null){
                throw new Exception("El cursor de salida es nulo");
            } 

            // Conversión del resultado en una lista de HorasCompensables
            List<HorasCompensables> data = (List<HorasCompensables>) result.get("p_cLISTADO");

            Long total = Long.parseLong(result.get("p_nTOTAL_RESULTADO").toString());

            // Creación del objeto DataTable con los resultados
            DataTable<HorasCompensables> dataTable = new DataTable<HorasCompensables>();
            dataTable.setData(data);
            dataTable.setRecordsTotal(total);
            dataTable.setRecordsFiltered(total);
            
            return dataTable;
        } catch (ValidacionPersonalizadaException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new Exception("Error al listar el metodo horas compensables");
        }
    }
}
