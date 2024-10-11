package gob.presidencia.siad.repository.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Tardanzas;
import gob.presidencia.siad.model.request.TardanzasListadoRequest;
import gob.presidencia.siad.repository.ITardanzasRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class TardanzasRepositoryImpl implements ITardanzasRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(TardanzasRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<Tardanzas> listar(int pageNo, int pageSize, TardanzasListadoRequest tardanzasListadoRequest) throws Exception {
		try {
			int offset = (pageNo - 1) * pageSize;
			
			logger.info("OFFSET --> " + offset );
			logger.info("PageSize --> " + pageSize );
			logger.info("PageNo --> " + pageNo );
			
			StringBuilder queryBuilder = new StringBuilder("SELECT X.*, ROWNUM FROM (SELECT \r\n"
					+ "t.cemp_codemp cempCodemp,t.nombre_empleado nombreEmpleado,t.dia_letra dia,to_char(t.entrada,'dd/mm/yyyy') fingreso,\r\n"
					+ "to_char(t.hora_turno,'hh24:mi') hingreso,to_char(t.entrada,'hh24:mi') ingreso\r\n"
					+ "FROM rh_v_tardanzas t \r\n"
					+ "WHERE ( rh_pk_asistencia.obten_es_tardanza(t.cemp_codemp,trunc(t.freg_fecregis))=0 or \r\n"
					+ "           rh_pk_asistencia.obten_tiempo_tardanza(t.cemp_codemp,trunc(t.freg_fecregis))> 0 \r\n"
					+ "     )  ");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("offset", offset);
			params.put("next", pageSize);
			
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getCodigoEmpleado())) {
				queryBuilder.append(" AND cemp_codemp = :cempCodemp ");
				params.put("cempCodemp", tardanzasListadoRequest.getCodigoEmpleado());
			}
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getFechaInicio())) {
				queryBuilder.append(" AND trunc(t.freg_fecregis) between trunc(to_date(:finicio,'yyyy-MM-dd')) ");
				params.put("finicio", tardanzasListadoRequest.getFechaInicio());
			}
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getFechaFin())) {
				queryBuilder.append(" AND trunc(to_date(:ffin,'yyyy-MM-dd'))  ");
				params.put("ffin", tardanzasListadoRequest.getFechaFin());
			}
			
			queryBuilder.append(" ORDER BY t.entrada) X OFFSET :offset ROWS FETCH NEXT :next ROWS ONLY");
			return namedParameterJdbcTemplate.query(queryBuilder.toString(), params, BeanPropertyRowMapper.newInstance(Tardanzas.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public Long totalListar(TardanzasListadoRequest tardanzasListadoRequest) throws Exception {
		try {
			
			StringBuilder queryBuilder = new StringBuilder("SELECT \r\n"
					+ "COUNT (*) \r\n"
					+ "FROM rh_v_tardanzas t \r\n"
					+ "WHERE ( rh_pk_asistencia.obten_es_tardanza(t.cemp_codemp,trunc(t.freg_fecregis))=0 or \r\n"
					+ "           rh_pk_asistencia.obten_tiempo_tardanza(t.cemp_codemp,trunc(t.freg_fecregis))> 0 \r\n"
					+ "     )  ");
			
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getCodigoEmpleado())) {
				queryBuilder.append(" AND cemp_codemp = :cempCodemp ");
				params.put("cempCodemp", tardanzasListadoRequest.getCodigoEmpleado());
			}
			
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getFechaInicio())) {
				queryBuilder.append(" AND trunc(t.freg_fecregis) between trunc(to_date(:finicio,'yyyy-MM-dd')) ");
				params.put("finicio", tardanzasListadoRequest.getFechaInicio());
			}
			
			if(!StringUtils.isBlank(tardanzasListadoRequest.getFechaFin())) {
				queryBuilder.append(" AND trunc(to_date(:ffin,'yyyy-MM-dd'))  ");
				params.put("ffin", tardanzasListadoRequest.getFechaFin());
			}	
			
			queryBuilder.append(" ORDER BY t.entrada");
			
			return namedParameterJdbcTemplate.queryForObject(queryBuilder.toString(), params, Long.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_OFI_TARD") 
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
	public List<Parametro> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_EMP_TARD") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_CODUSU", codigoUsuario)
		        .addValue("p_vV_CODOFI", codigoOficina)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
	    	return (List<Parametro>) result.get("p_cLISTADO");
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar empleados");
		}
	}

	
}
