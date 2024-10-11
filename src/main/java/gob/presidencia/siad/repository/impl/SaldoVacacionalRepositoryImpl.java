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
import gob.presidencia.siad.model.SaldoVacacional;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;
import gob.presidencia.siad.repository.ISaldoVacacionalRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class SaldoVacacionalRepositoryImpl  implements ISaldoVacacionalRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(SaldoVacacionalRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario, String codigoEmpleado) throws Exception {
		try {
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

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_CODUSU", codigoUsuario)
		        .addValue("p_vV_CEMP_CODEMP", codigoEmpleado)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
	    	return (List<Parametro>) result.get("p_cLISTADO");
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar oficinas");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataTable<SaldoVacacional> listarSP(
		int pageNo, 
		int pageSize, 
		SaldoVacacionalListadoRequest saldoVacacionalRequest) throws Exception {
		try {
			int offset = (pageNo - 1) * pageSize;
			
			logger.info("OFFSET --> " + offset );
			logger.info("PageSize --> " + pageSize );
			logger.info("PageNo --> " + pageNo );
			
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_SALDO_VAC")
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_NOMEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_OFFSET", Types.VARCHAR),
			            new SqlParameter("p_vV_SIZE", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_nTOTAL_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", saldoVacacionalRequest.getAnio())
		        .addValue("p_vV_CODOFI", saldoVacacionalRequest.getCodigoOficina())
		        .addValue("p_vV_NOMEMP", saldoVacacionalRequest.getEmpleado())
		        .addValue("p_vV_CODUSU", saldoVacacionalRequest.getCodigoUsuario())
		        .addValue("p_vV_CEMP_CODEMP", saldoVacacionalRequest.getCodigoEmpleado())
		        .addValue("p_vV_OFFSET", offset)
		        .addValue("p_vV_SIZE", pageSize)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null || result.get("p_nTOTAL_RESULTADO")==null) throw new Exception("Respuesta esperada nula");
	    	List<SaldoVacacional> data = (List<SaldoVacacional>) result.get("p_cLISTADO");
	    	Long total = Long.parseLong(result.get("p_nTOTAL_RESULTADO").toString());
	    	DataTable<SaldoVacacional> dataTable = new DataTable<SaldoVacacional>();
	    	dataTable.setData(data);
	    	dataTable.setRecordsTotal(total);
	    	dataTable.setRecordsFiltered(total);
	    	return dataTable;
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar oficinas");
		}
	}
	
} 