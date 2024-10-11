package gob.presidencia.siad.repository.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.NotaAlimentoException;
import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoEstado;
import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.NotaAlimentoDetalle;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.NotaAlimentoListadoRequest;
import gob.presidencia.siad.repository.INotasAlimentosRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class NotasAlimentosRepositoryImpl implements INotasAlimentosRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(NotasAlimentosRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<NotaAlimento> listar(int pageNo, int pageSize, NotaAlimentoListadoRequest notaAlimentoListadoRequest) throws Exception {
		try {
			int offset = (pageNo - 1) * pageSize;
			
			StringBuilder queryBuilder = new StringBuilder("SELECT X.*, ROWNUM FROM (SELECT \r\n"
					+ "A.CANO_ANOSOL, A.CDOC_TIPDOC, A.CSOL_NUMSOL,\r\n"
					+ "B.CDOC_DESCOR tipoDocumento,\r\n"
					+ "A.CSOL_NUMSOL numeroSolicitud, \r\n"
					+ "TO_CHAR(A.DSOL_FECSOL, 'DD/MM/YYYY') fechaSolicitud,\r\n"
					+ "DECODE(A.CSOL_TIPSOL,	'01', 'ATENCIÓN',	'02', 'JUSTIFICACIÓN') tipoSolicitud,\r\n"
					+ "C.COFI_NOMLAR nombreOficina,\r\n"
					+ "TO_CHAR(A.DSOL_FECINI, 'DD/MM/YYYY') fechaInicioSolicitud,\r\n"
					+ "TO_CHAR(A.DSOL_FECFIN, 'DD/MM/YYYY') fechaFinSolicitud,\r\n"
					+ "D.CEST_DESCOR estado\r\n"
					+ "FROM SIGA.AL_ALI_SOLICITUD A \r\n"
					+ "LEFT JOIN SIGA.SI_MAE_TIPO_DOC B ON B.CDOC_NOMTAB = 'AL_ALI_SOLICITUD' AND B.CDOC_TIPDOC = A.CDOC_TIPDOC\r\n"
					+ "LEFT JOIN SIGA.SI_MAE_OFICINAS C ON C.COFI_CODOFI = A.COFI_CODOFI\r\n"
					+ "LEFT JOIN SIGA.SI_MAE_ESTADOS D ON D.CEST_NOMTAB = 'AL_ALI_SOLICITUD' AND D.CEST_CODEST = A.CSOL_ESTADO\r\n"
					+ "WHERE 1=1 ");
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("offset", offset);
			params.put("next", pageSize);
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getFechaNotaInicio())) {
				queryBuilder.append(" AND TRUNC(A.DSOL_FECSOL) >= TO_DATE(:fechaNotaInicio,'YYYY-MM-DD')");
				params.put("fechaNotaInicio", notaAlimentoListadoRequest.getFechaNotaInicio());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getFechaNotaFin())) {
				queryBuilder.append(" AND TRUNC(A.DSOL_FECSOL) <= TO_DATE(:fechaNotaFin,'YYYY-MM-DD')");
				params.put("fechaNotaFin", notaAlimentoListadoRequest.getFechaNotaFin());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoOficina())) {
				queryBuilder.append(" AND A.COFI_CODOFI = :codigoOficina");
				params.put("codigoOficina", notaAlimentoListadoRequest.getCodigoOficina());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoTipoDocumento())) {
				queryBuilder.append(" AND A.CDOC_TIPDOC = :codigoTipoDocumento");
				params.put("codigoTipoDocumento", notaAlimentoListadoRequest.getCodigoTipoDocumento());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoTipoNota())) {
				queryBuilder.append(" AND A.CSOL_TIPSOL = :codigoTipoNota");
				params.put("codigoTipoNota", notaAlimentoListadoRequest.getCodigoTipoNota());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoEstado())) {
				queryBuilder.append(" AND A.CSOL_ESTADO = :codigoEstado");
				params.put("codigoEstado", notaAlimentoListadoRequest.getCodigoEstado());
			}
			
			queryBuilder.append(" ORDER BY A.DSOL_FECSOL DESC) X OFFSET :offset ROWS FETCH NEXT :next ROWS ONLY");
			return namedParameterJdbcTemplate.query(queryBuilder.toString(), params, BeanPropertyRowMapper.newInstance(NotaAlimento.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public Long totalListar(NotaAlimentoListadoRequest notaAlimentoListadoRequest) throws Exception {
		try {
			
			StringBuilder queryBuilder = new StringBuilder("SELECT \r\n"
					+ "COUNT(A.CSOL_NUMSOL)\r\n"
					+ "FROM SIGA.AL_ALI_SOLICITUD A \r\n"
					+ "LEFT JOIN SIGA.SI_MAE_TIPO_DOC B ON B.CDOC_NOMTAB = 'AL_ALI_SOLICITUD' AND B.CDOC_TIPDOC = A.CDOC_TIPDOC\r\n"
					+ "LEFT JOIN SIGA.SI_MAE_OFICINAS C ON C.COFI_CODOFI = A.COFI_CODOFI\r\n"
					+ "LEFT JOIN SIGA.SI_MAE_ESTADOS D ON D.CEST_NOMTAB = 'AL_ALI_SOLICITUD' AND D.CEST_CODEST = A.CSOL_ESTADO\r\n"
					+ "WHERE 1=1 ");
			
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getFechaNotaInicio())) {
				queryBuilder.append(" AND TRUNC(A.DSOL_FECSOL) >= TO_DATE(:fechaNotaInicio,'YYYY-MM-DD')");
				params.put("fechaNotaInicio", notaAlimentoListadoRequest.getFechaNotaInicio());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getFechaNotaFin())) {
				queryBuilder.append(" AND TRUNC(A.DSOL_FECSOL) <= TO_DATE(:fechaNotaFin,'YYYY-MM-DD')");
				params.put("fechaNotaFin", notaAlimentoListadoRequest.getFechaNotaFin());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoOficina())) {
				queryBuilder.append(" AND A.COFI_CODOFI = :codigoOficina");
				params.put("codigoOficina", notaAlimentoListadoRequest.getCodigoOficina());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoTipoDocumento())) {
				queryBuilder.append(" AND A.CDOC_TIPDOC = :codigoTipoDocumento");
				params.put("codigoTipoDocumento", notaAlimentoListadoRequest.getCodigoTipoDocumento());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoTipoNota())) {
				queryBuilder.append(" AND A.CSOL_TIPSOL = :codigoTipoNota");
				params.put("codigoTipoNota", notaAlimentoListadoRequest.getCodigoTipoNota());
			}
			
			if(!StringUtils.isBlank(notaAlimentoListadoRequest.getCodigoEstado())) {
				queryBuilder.append(" AND A.CSOL_ESTADO = :codigoEstado");
				params.put("codigoEstado", notaAlimentoListadoRequest.getCodigoEstado());
			}		
			
			queryBuilder.append(" ORDER BY A.DSOL_FECSOL DESC");
			return namedParameterJdbcTemplate.queryForObject(queryBuilder.toString(), params, Long.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	@Override
	public String insertarSP(NotaAlimento notaAlimento) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_REG_NOTA_ALIM") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_OBS", Types.VARCHAR),
			            new SqlParameter("p_vV_INDAPO", Types.VARCHAR),
			            new SqlParameter("p_vV_SSOLMOTIVO", Types.VARCHAR),
			            new SqlParameter("p_vV_CSOLPROCE", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_vID", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimento.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimento.getCdocTipdoc())
		        .addValue("p_vV_FECHA", notaAlimento.getDsolFecsol())
		        .addValue("p_vV_FECHA_INI", notaAlimento.getDsolFecini())
		        .addValue("p_vV_FECHA_FIN", notaAlimento.getDsolFecfin())
		        .addValue("p_vV_CODOFI", notaAlimento.getCofiCodofi())
		        .addValue("p_vV_OBS", notaAlimento.getCsolObssol())
		        .addValue("p_vV_INDAPO", notaAlimento.getCsolIndapo())
		        .addValue("p_vV_SSOLMOTIVO", notaAlimento.getSsolMotivo())
		        .addValue("p_vV_CSOLPROCE", notaAlimento.getCsolProce())
		        .addValue("p_vV_CODUSU", notaAlimento.getCusuUsureg())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		
		    if(result.get("p_vID")==null) throw new Exception("Error al insertar cabecera: ID esperado nulo");
	    	return String.valueOf(result.get("p_vID"));
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al insertar la nota de alimento");
		}
	}
	
	@Override
	public void insertarDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_REG_NOTA_ALIM_DET") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_vV_CHOR_CODTIPO", Types.VARCHAR),
			            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_OBS", Types.VARCHAR),
			            new SqlParameter("p_vV_CODAPO", Types.VARCHAR),
			            new SqlParameter("p_nN_CANT", Types.INTEGER),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimentoDetalle.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimentoDetalle.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimentoDetalle.getCsolNumsol())
		        .addValue("p_vV_CHOR_CODTIPO", notaAlimentoDetalle.getChorCodtipo())
		        .addValue("p_vV_CEMP_CODEMP", notaAlimentoDetalle.getCempCodemp())
		        .addValue("p_vV_CODOFI", notaAlimentoDetalle.getCofiCodofi())
		        .addValue("p_vV_OBS", notaAlimentoDetalle.getCsodSodobs())
		        .addValue("p_vV_CODAPO", notaAlimentoDetalle.getCapoCodapo())
		        .addValue("p_nN_CANT", notaAlimentoDetalle.getNsodCanper())
		        .addValue("p_vV_CODUSU", notaAlimentoDetalle.getCusuUsureg())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;    
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al insertar el detalle de nota de alimento");
		}
	}
	
	@Override
	public void actualizarSP(NotaAlimento notaAlimento) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_MOD_NOTA_ALIM") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_OBS", Types.VARCHAR),
			            new SqlParameter("p_vV_CSOLPROCE", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimento.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimento.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimento.getCsolNumsol())
		        .addValue("p_vV_FECHA", notaAlimento.getDsolFecsol())
		        .addValue("p_vV_FECHA_INI", notaAlimento.getDsolFecini())
		        .addValue("p_vV_FECHA_FIN", notaAlimento.getDsolFecfin())
		        .addValue("p_vV_CODOFI", notaAlimento.getCofiCodofi())
		        .addValue("p_vV_OBS", notaAlimento.getCsolObssol())
		        .addValue("p_vV_CSOLPROCE", notaAlimento.getCsolProce())
		        .addValue("p_vV_CODUSU", notaAlimento.getCusuUsumod())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al actualizar la nota de alimento");
		}
	}
	
	@Override
	public void actualizarDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_MOD_NOTA_ALIM_DET") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_nN_NUMITEM", Types.INTEGER),
			            new SqlParameter("p_vV_CHOR_CODTIPO", Types.VARCHAR),
			            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_OBS", Types.VARCHAR),
			            new SqlParameter("p_vV_CODAPO", Types.VARCHAR),
			            new SqlParameter("p_nN_CANT", Types.INTEGER),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimentoDetalle.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimentoDetalle.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimentoDetalle.getCsolNumsol())
		        .addValue("p_nN_NUMITEM", notaAlimentoDetalle.getNsodItem())
		        .addValue("p_vV_CHOR_CODTIPO", notaAlimentoDetalle.getChorCodtipo())
		        .addValue("p_vV_CEMP_CODEMP", notaAlimentoDetalle.getCempCodemp())
		        .addValue("p_vV_CODOFI", notaAlimentoDetalle.getCofiCodofi())
		        .addValue("p_vV_OBS", notaAlimentoDetalle.getCsodSodobs())
		        .addValue("p_vV_CODAPO", notaAlimentoDetalle.getCapoCodapo())
		        .addValue("p_nN_CANT", notaAlimentoDetalle.getNsodCanper())
		        .addValue("p_vV_CODUSU", notaAlimentoDetalle.getCusuUsumod())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al actualizar el detalle de nota de alimento");
		}
	}
	
	@Override
	public void anularSP(NotaAlimento notaAlimento) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_ANU_NOTA_ALIM") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimento.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimento.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimento.getCsolNumsol())
		        .addValue("p_vV_CODUSU", notaAlimento.getCusuUsumod())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al anular la nota de alimento");
		}
	}
	
	@Override
	public void anularDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_ANU_NOTA_ALIM_DET") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_nN_NUMITEM", Types.INTEGER),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimentoDetalle.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimentoDetalle.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimentoDetalle.getCsolNumsol())
		        .addValue("p_nN_NUMITEM", notaAlimentoDetalle.getNsodItem())
		        .addValue("p_vV_CODUSU", notaAlimentoDetalle.getCusuUsumod())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (NotaAlimentoException e) {
			throw e;    
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al anular el detalle de nota de alimento");
		}
	}
	
	@Override
	public void aprobarSP(NotaAlimento notaAlimento) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_APR_NOTA_ALIM") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_TIPO_DOC", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_ANIO", notaAlimento.getCanoAnosol())
		        .addValue("p_vV_TIPO_DOC", notaAlimento.getCdocTipdoc())
		        .addValue("p_vV_NUMSOL", notaAlimento.getCsolNumsol())
		        .addValue("p_vV_CODUSU", notaAlimento.getCusuUsumod())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al aprobar la nota de alimento");
		}
	}
	
	@Override
	public String esAprobadorSP(NotaAlimento notaAlimento) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_ES_APR_NOTA_ALIM") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_vFLAG_RESP", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("p_vV_CODOFI", notaAlimento.getCofiCodofi())
		        .addValue("p_vV_CODUSU", notaAlimento.getCusuUsureg())
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    if(result.get("p_vFLAG_RESP")==null) throw new Exception("Respuesta esperada nula");
		    return String.valueOf(result.get("p_vFLAG_RESP"));
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al cargar rol de aprobador de la nota de alimento");
		}
	}

	@Override
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		try {
			String sql = "SELECT DISTINCT COFI_NOMLAR nombre, A.COFI_CODOFI codigo\r\n"
					+ "FROM SI_PER_OFICINAS A, SI_PED_AUTORIZACION B \r\n"
					+ "WHERE nvl(a.cofi_indvig,'0') = '0' \r\n"
					+ "and A.CANO_ANOEJE=B.CANO_ANOEJE \r\n"
					+ "AND A.CPER_PEREJE=B.CPER_PEREJE \r\n"
					+ "AND A.CINS_CODINS=B.CINS_CODINS \r\n"
					+ "AND A.COFI_CODOFI=B.COFI_CODOFI \r\n"
					+ "AND ( B.CUSU_CODUSU= ? OR \r\n"
					+ "EXISTS\r\n"
					+ "(SELECT * FROM SI_PED_AUTODELE C\r\n"
					+ "WHERE C.cano_anoeje =b.cano_anoeje\r\n"
					+ "and C.cper_pereje =b.cper_pereje\r\n"
					+ "and C.cins_codins =b.cins_codins\r\n"
					+ "and C.cope_codope =b.cope_codope\r\n"
					+ "and C.cope_opeflu =b.cope_opeflu\r\n"
					+ "and C.crol_codrol =b.crol_codrol\r\n"
					+ "and C.cusu_codusu =b.cusu_codusu \r\n"
					+ "and C.cofi_codofi =b.cofi_codofi\r\n"
					+ "and  cdel_empdel = ?\r\n"
					+ "and C.cdel_indvig ='0'))\r\n"
					+ "AND B.COPE_CODOPE= 'OP015'\r\n"
					+ "AND B.CANO_ANOEJE= ?\r\n"
					+ "AND B.CPER_PEREJE= '01'\r\n"
					+ "AND B.CINS_CODINS= 102\r\n"
					+ "AND B.CPED_INDVIG='0'";
			Object[] params = new Object[]{codigoUsuario, codigoUsuario, anio};
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parametro.class), params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar las oficinas");
		}
	}
	
	@Override
	public List<Parametro> listarTipoDocumentos() throws Exception {
		try {
			String sql = "SELECT CDOC_TIPDOC codigo, CDOC_DESDOC nombre FROM SIGA.SI_MAE_TIPO_DOC WHERE CDOC_NOMTAB = 'AL_ALI_SOLICITUD'";
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parametro.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los tipos de documentos");
		}
	}

	@Override
	public List<Parametro> listarTipoNotas() throws Exception {
		try {
			String sql = "SELECT '01' codigo, 'ATENCION' nombre FROM DUAL\r\n"
					+ "UNION\r\n"
					+ "SELECT '02' codigo, 'JUSTIFICACION' nombre FROM DUAL";
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parametro.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los tipos de notas");
		}
	}

	@Override
	public List<Parametro> listarEstados() throws Exception {
		try {
			String sql = "SELECT CEST_CODEST codigo, CEST_DESCOR nombre FROM SIGA.SI_MAE_ESTADOS A where A.CEST_NOMTAB='AL_ALI_SOLICITUD'";
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Parametro.class));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los estados");
		}
	}

	@Override
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception {
		try {
			String sql = "select cemp_codemp,cemp_apepat||' '||cemp_apemat||' '||cemp_prinom nombre , CEMP_FOTOCHECK FOTOCHECK , cind_comedor,\r\n"
					+ "nvl((select  sum(dsol_fecfin- dsol_fecini +1)\r\n"
					+ "from al_ali_solicitud a, al_ali_solicitud_det b, RH_PER_EMPLEADOS J\r\n"
					+ "    where B.CEMP_CODEMP=J.CEMP_CODEMP(+)\r\n"
					+ "    AND a.CANO_ANOSOL=b.CANO_ANOSOL\r\n"
					+ "    and a.CDOC_TIPDOC=b.CDOC_TIPDOC\r\n"
					+ "    and a.CSOL_NUMSOL=b.CSOL_NUMSOL\r\n"
					+ "    and csol_estado = '03' \r\n"
					+ "    AND csol_tipsol='02' \r\n"
					+ "    and to_char(dsol_fecini,'mm-yyyy')=to_char(sysdate,'mm-yyyy')\r\n"
					+ "    and b.cemp_codemp=r.cemp_codemp),0) jus\r\n"
					+ "from rh_per_empleados r\r\n"
					+ "where cofi_codofi = ?\r\n"
					+ "and (cemp_indbaj='0' or femp_fecbaj >= sysdate)\r\n"
					+ "order by cemp_apepat";
			Object[] params = new Object[]{codigoOficina};
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Empleado.class), params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los empleados por oficina " + codigoOficina);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Empleado> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina, String indicadorApoyo) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_EMP_ALIM")
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_INDAPO", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
			            new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_ANIO", anio)
		        .addValue("p_vV_CODUSU", codigoUsuario)
		        .addValue("p_vV_CODOFI", codigoOficina)
		        .addValue("p_vV_INDAPO", indicadorApoyo)
		        ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_cLISTADO")==null) throw new Exception("Respuesta esperada nula");
		    List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
		    return repositoryUtil.castToList(Empleado.class, list);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al listar empleados");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarTipoRacionesSP() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_RACION_ALIM") 
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
			throw new Exception("Error al listar empleados");
		}
	}

	@Override
	public NotaAlimento obtenerNotaAlimento(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception {
		try {
			String sql = "SELECT "
					+ "CANO_ANOSOL, CDOC_TIPDOC, CSOL_NUMSOL, B.CEST_DESCOR ESTADO, CSOL_ESTADO, TO_CHAR(DSOL_FECSOL,'YYYY-MM-DD') DSOL_FECSOL, TO_CHAR(DSOL_FECINI,'YYYY-MM-DD') DSOL_FECINI, " 
					+ "TO_CHAR(DSOL_FECFIN,'YYYY-MM-DD') DSOL_FECFIN, COFI_CODOFI, CSOL_OBSSOL, CSOL_INDAPO, SSOL_MOTIVO, CSOL_PROCE "
					+ "FROM SIGA.AL_ALI_SOLICITUD A "
					+ "LEFT JOIN SIGA.SI_MAE_ESTADOS B ON B.CEST_NOMTAB = 'AL_ALI_SOLICITUD' AND B.CEST_CODEST = A.CSOL_ESTADO "
					+ "WHERE A.CANO_ANOSOL = ? AND A.CDOC_TIPDOC = ? AND A.CSOL_NUMSOL = ? ";
			Object[] params = new Object[]{anio,codigoTipoDocumento,numeroSolicitud};
			
			NotaAlimento notaAlimento = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(NotaAlimento.class), params).stream().findFirst().orElse(null);
			if(notaAlimento!=null) {
				List<NotaAlimentoDetalle> items = null;
				try {
					String sqlDet = "SELECT "
							+ "A.CANO_ANOSOL, A.CDOC_TIPDOC, A.CSOL_NUMSOL, A.NSOD_ITEM, A.CHOR_CODTIPO, A.CEMP_CODEMP, A.CAPO_CODAPO, A.COFI_CODOFI, A.CSOD_SODOBS, A.NSOD_CANPER, B.CHOR_DESCTIPO DESCRIPCIONRACION, "
							+ "C.CEMP_APEPAT||' '||C.CEMP_APEMAT||' '||C.CEMP_PRINOM NOMBRES, C.CEMP_FOTOCHECK FOTOCHECK "
							+ "FROM SIGA.AL_ALI_SOLICITUD_DET A "
							+ "LEFT JOIN SIGA.RH_COM_HORARIO B ON A.CHOR_CODTIPO = B.CHOR_CODTIPO "
							+ "LEFT JOIN SIGA.RH_PER_EMPLEADOS C ON A.CEMP_CODEMP = C.CEMP_CODEMP "
							+ "WHERE A.CANO_ANOSOL = ? AND A.CDOC_TIPDOC = ? AND A.CSOL_NUMSOL = ? ORDER BY A.NSOD_ITEM";
					if(notaAlimento.getCsolIndapo()!=null && notaAlimento.getCsolIndapo().equals("1")) {
						sqlDet = "SELECT "
								+ "A.CANO_ANOSOL, A.CDOC_TIPDOC, A.CSOL_NUMSOL, A.NSOD_ITEM, A.CHOR_CODTIPO, A.CEMP_CODEMP, A.CAPO_CODAPO, A.COFI_CODOFI, A.CSOD_SODOBS, A.NSOD_CANPER, B.CHOR_DESCTIPO DESCRIPCIONRACION, "
								+ "C.CAPO_NOMBRE NOMBRES, C.COFI_CODOFI||C.CAPO_CODAPO FOTOCHECK "
								+ "FROM SIGA.AL_ALI_SOLICITUD_DET A "
								+ "LEFT JOIN SIGA.RH_COM_HORARIO B ON A.CHOR_CODTIPO = B.CHOR_CODTIPO "
								+ "LEFT JOIN SIGA.AL_PER_APOYO C ON A.CAPO_CODAPO = C.CAPO_CODAPO AND A.COFI_CODOFI = C.COFI_CODOFI "
								+ "WHERE A.CANO_ANOSOL = ? AND A.CDOC_TIPDOC = ? AND A.CSOL_NUMSOL = ? ORDER BY A.NSOD_ITEM";
					}
					items = jdbcTemplate.query(sqlDet, BeanPropertyRowMapper.newInstance(NotaAlimentoDetalle.class), params);
				} catch (EmptyResultDataAccessException e) {
					items = new ArrayList<>();
				}
				notaAlimento.setItems(items);
			}
			return notaAlimento;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar la nota: " + anio + "-" + codigoTipoDocumento + "-" + numeroSolicitud);
		}
	}

	@Override
	public List<FlujoEstado> listarFlujoEstados(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception {
		try {
			String sql = "SELECT B.CUSU_CODUSU codigoUsuario, C.CEST_DESCOR descripcionEstado, TO_CHAR(A.DFEC_CRE, 'DD/MM/YYYY') fechaEstado FROM SIGA.LG_ABA_PEDAUTOR A "
					+ "LEFT JOIN SIGA.SI_USUARIO B ON A.CEMP_CODEMP = B.CEMP_CODEMP AND B.CEMP_REAL IS NULL AND B.DFEC_FECSAL IS NULL "
					+ "INNER JOIN SIGA.SI_MAE_ESTADOS C ON C.CEST_NOMTAB='AL_ALI_SOLICITUD' AND A.CPED_CODEST = C.CEST_CODEST "
					+ "WHERE "
					+ "A.cano_anoeje = ? and "
					+ "A.cper_pereje = '01' and "
					+ "A.cins_codins = 102 and "
					+ "A.cped_desmot = 'OP015' and "
					+ "A.cped_numped = ?||? "
					+ "order by A.CPED_CODEST";
			Object[] params = new Object[]{anio,codigoTipoDocumento,numeroSolicitud};
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(FlujoEstado.class), params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los flujos de estados de la nota: " + anio + "-" + codigoTipoDocumento + "-" + numeroSolicitud);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarTipoMotivosSP() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_MOTIVO_ALIM") 
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
			throw new Exception("Error al listar empleados");
		}
	}

	
}
