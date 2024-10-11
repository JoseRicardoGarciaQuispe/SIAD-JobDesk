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

import gob.presidencia.siad.exception.ExportarDataException;
import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Reporte;
import gob.presidencia.siad.model.request.ExportarDataListadoRequest;
import gob.presidencia.siad.model.request.GenerarReporteRequest;
import gob.presidencia.siad.repository.IExportarDataRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class ExportarDataRepositoryImpl  implements IExportarDataRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ExportarDataRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	public DataTable<Reporte> listarSP(int pageNo, int pageSize, ExportarDataListadoRequest exportarDataListadoRequest) throws Exception {
		try {
			int offset = (pageNo - 1) * pageSize;
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_LISTAR_REPORTES") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlParameter("p_vV_DESCR", Types.VARCHAR),
			            new SqlParameter("p_vV_OFFSET", Types.VARCHAR),
			            new SqlParameter("p_vV_SIZE", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_nTOTAL_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_CODUSU", exportarDataListadoRequest.getUsuario())
		        .addValue("p_vV_DESCR", exportarDataListadoRequest.getDescripcion())
		        .addValue("p_vV_OFFSET", offset)
		        .addValue("p_vV_SIZE", pageSize)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null || result.get("p_nTOTAL_RESULTADO")==null) throw new Exception("Respuesta esperada nula");
	    	List<Reporte> data = (List<Reporte>) result.get("p_cLISTADO");
	    	Long total = Long.parseLong(result.get("p_nTOTAL_RESULTADO").toString());
	    	DataTable<Reporte> dataTable = new DataTable<Reporte>();
	    	dataTable.setData(data);
	    	dataTable.setRecordsTotal(total);
	    	dataTable.setRecordsFiltered(total);
	    	return dataTable;
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar los reportes");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Reporte obtenerReporteSP(String id) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_OBTENER_REPORTE") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_CORREL", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_CORREL", id)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
	    	List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
	    	List<Reporte> lista = repositoryUtil.castToList(Reporte.class, list);
	    	if(lista.isEmpty()) throw new ValidacionPersonalizadaException("Registro no encontrado");
	    	return lista.get(0);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al obtener el reporte");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> generarListadoReporteSP(GenerarReporteRequest generarReporteRequest) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_GENERAR_LIST_REPORTE") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_CORREL", Types.VARCHAR),
			            new SqlParameter("p_vP1", Types.VARCHAR),
			            new SqlParameter("p_vP2", Types.VARCHAR),
			            new SqlParameter("p_vP3", Types.VARCHAR),
			            new SqlParameter("p_vP4", Types.VARCHAR),
			            new SqlParameter("p_vP5", Types.VARCHAR),
			            new SqlParameter("p_vP6", Types.VARCHAR),
			            new SqlParameter("p_vP7", Types.VARCHAR),
			            new SqlParameter("p_vP8", Types.VARCHAR),
			            new SqlParameter("p_vP9", Types.VARCHAR),
			            new SqlParameter("p_vPX", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_CORREL", generarReporteRequest.getId())
		        .addValue("p_vP1", generarReporteRequest.getP1())
		        .addValue("p_vP2", generarReporteRequest.getP2())
		        .addValue("p_vP3", generarReporteRequest.getP3())
		        .addValue("p_vP4", generarReporteRequest.getP4())
		        .addValue("p_vP5", generarReporteRequest.getP5())
		        .addValue("p_vP6", generarReporteRequest.getP6())
		        .addValue("p_vP7", generarReporteRequest.getP7())
		        .addValue("p_vP8", repositoryUtil.castDateFormat(generarReporteRequest.getP8()))
		        .addValue("p_vP9", repositoryUtil.castDateFormat(generarReporteRequest.getP9()))
		        .addValue("p_vPX", generarReporteRequest.getPx())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    return (List<Map<String, Object>>) result.get("p_cLISTADO");
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al generar el reporte");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarGruposSP(String anio, String termino) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_BUSCAR_GRUPOS") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TERM", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_TERM", termino)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    return (List<Parametro>) result.get("p_cLISTADO");
		} catch (ExportarDataException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar grupos");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarArticulosSP(String anio, String termino) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_BUSCAR_ARTICULOS") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TERM", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_TERM", termino)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    return (List<Parametro>) result.get("p_cLISTADO");
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar articulos");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> buscarClasesSP(String anio, String codgrupo, String termino) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_BUSCAR_CLASES") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_CODGRU", Types.VARCHAR),
			            new SqlParameter("p_vV_TERM", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_CODGRU", codgrupo)
		        .addValue("p_vV_TERM", termino)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    return (List<Parametro>) result.get("p_cLISTADO");
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar clases");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_LISTAR_OFI_EXPORTAR") 
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
	public List<Parametro> listarAniosSP() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_EXP_DATA.SIADW_SP_LISTAR_ANIOS_EXPORTAR") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    Map<String, Object> result = jdbcCall.execute();
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
		    return repositoryUtil.castToList(Parametro.class, list);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar años");
		}
	}
	
	
} 