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
import gob.presidencia.siad.model.AccesoAlimentos;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;
import gob.presidencia.siad.repository.IAccesoAlimentosRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class AccesoAlimentosRepositoryImpl  implements IAccesoAlimentosRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(AccesoAlimentosRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_OFI_ALIM")
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_CODUSU", codigoUsuario)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
		    return repositoryUtil.castToList(Parametro.class, list);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar oficinas");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DataTable<AccesoAlimentos> listarSP(int pageNo, int pageSize, AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) throws Exception {
		try {
			int offset = (pageNo - 1) * pageSize;
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_ACCESO_ALIM")
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			        	new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_NOMEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_ESTADO", Types.VARCHAR),
			            new SqlParameter("p_vV_OFFSET", Types.VARCHAR),
			            new SqlParameter("p_vV_SIZE", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_nTOTAL_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		    	.addValue("p_vV_FECHA", accesoAlimentosListadoRequest.getFecha())
		    	.addValue("p_vV_CODOFI", accesoAlimentosListadoRequest.getCodigoOficina())
		        .addValue("p_vV_NOMEMP", accesoAlimentosListadoRequest.getEmpleado())
		        .addValue("p_vV_ESTADO", accesoAlimentosListadoRequest.getEstado())
		        .addValue("p_vV_OFFSET", offset)
		        .addValue("p_vV_SIZE", pageSize)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null || result.get("p_nTOTAL_RESULTADO")==null) throw new Exception("Respuesta esperada nula");
	    	List<AccesoAlimentos> data = (List<AccesoAlimentos>) result.get("p_cLISTADO");
	    	Long total = Long.parseLong(result.get("p_nTOTAL_RESULTADO").toString());
	    	DataTable<AccesoAlimentos> dataTable = new DataTable<AccesoAlimentos>();
	    	dataTable.setData(data);
	    	dataTable.setRecordsTotal(total);
	    	dataTable.setRecordsFiltered(total);
	    	return dataTable;
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar accesos de alimentos");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AccesoAlimentos> listarAllSP(AccesoAlimentosListadoRequest accesoAlimentosListadoRequest)
			throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
					.withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_ACCESO_ALIM")
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			        	new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_NOMEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_ESTADO", Types.VARCHAR),
			            new SqlParameter("p_vV_OFFSET", Types.VARCHAR),
			            new SqlParameter("p_vV_SIZE", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_nTOTAL_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		    	.addValue("p_vV_FECHA", accesoAlimentosListadoRequest.getFecha())
		    	.addValue("p_vV_CODOFI", accesoAlimentosListadoRequest.getCodigoOficina())
		        .addValue("p_vV_NOMEMP", accesoAlimentosListadoRequest.getEmpleado())
		        .addValue("p_vV_ESTADO", accesoAlimentosListadoRequest.getEstado())
		        .addValue("p_vV_OFFSET", null)
		        .addValue("p_vV_SIZE", null)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null || result.get("p_nTOTAL_RESULTADO")==null) throw new Exception("Respuesta esperada nula");
	    	List<AccesoAlimentos> data = (List<AccesoAlimentos>) result.get("p_cLISTADO");
	    	return data;
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar accesos de alimentos 2");
		}
	}
	
} 