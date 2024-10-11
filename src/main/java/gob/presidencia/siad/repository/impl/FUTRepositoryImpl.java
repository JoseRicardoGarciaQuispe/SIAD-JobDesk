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
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoAprobacion;

import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.FutListadoRequest;
import gob.presidencia.siad.repository.IFUTRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class FUTRepositoryImpl implements IFUTRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(FUTRepositoryImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;

	
	
	/* LISTA DE REGISTROS */
	@Override
	public List<Fut> listar(int pageNo, int pageSize, FutListadoRequest futListadoRequest) throws Exception {
		try {
			
			logger.info("Ingresando al query para listar los registros de FUT");
			
			int offset = (pageNo -1) * pageSize;
			
			StringBuilder queryBuilder = new StringBuilder("SELECT X.*, ROWNUM FROM ( SELECT A.CANO_ANOEJE canoAnosol, "
					+ " A.NLIC_NUMSEC numeroFut, "
					+ "B.CEMP_APEPAT || ' ' || B.CEMP_APEMAT || ', ' || B.CEMP_PRINOM || ' ' || B.CEMP_SEGNOM nomEmpleado, "
					+ "C.COFI_NOMCOR nombreOficina, "
					+ "A.FLIC_FECINI || ' ' || (TO_CHAR(A.FLIC_HORINI, 'HH24:MI')) fechaInicioFut, "
					+ "A.FLIC_FECFIN || ' ' || (TO_CHAR(A.FLIC_HORFIN, 'HH24:MI')) fechaFinFut, "
					+ "TRUNC(A.FLIC_FECFIN) - TRUNC(A.FLIC_FECINI) + 1 numeroDias, "
					+ "TO_CHAR(A.DFEC_FUT) fechaSolicitudFut, D.CMOT_DESMOT motivo, E.CEST_DESLAR estado "
					+ "FROM RH_PER_LICENCIAS A "
					+ "LEFT JOIN RH_PER_EMPLEADOS B ON A.CEMP_CODEMP = B.CEMP_CODEMP "
					+ "LEFT JOIN SI_MAE_OFICINAS C ON A.COFI_CODOFI = C.COFI_CODOFI "
					+ "LEFT JOIN RH_PER_MOTIVOLICE D ON A.CMOT_CODMOT = D.CMOT_CODMOT "
					+ "LEFT JOIN SI_MAE_ESTADOS E ON A.CFUT_ESTADO = E.CEST_CODEST "
					+ "WHERE E.CEST_NOMTAB='RH_PER_LICENCIAS' ");
			
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("offset", offset);
				params.put("next", pageSize);
				
				if(!StringUtils.isBlank(futListadoRequest.getFechaFutInicio())) {
					queryBuilder.append( " AND A.DFEC_FUT >= TO_DATE(:fechaFutInicio,'yyyy-MM-dd')");
					params.put("fechaFutInicio", futListadoRequest.getFechaFutInicio());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getFechaFutFin())) {
					queryBuilder.append( " AND A.DFEC_FUT <= TO_DATE(:fechaFutFin,'yyyy-MM-dd')");
					params.put("fechaFutFin", futListadoRequest.getFechaFutFin());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getFechaLicInicio())) {
					queryBuilder.append( " AND A.FLIC_FECINI >= TO_DATE(:fechaLicInicio,'yyyy-MM-dd')");
					params.put("fechaLicInicio", futListadoRequest.getFechaLicInicio());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getFechaLicFin())) {
					queryBuilder.append( " AND A.FLIC_FECFIN <= TO_DATE(:fechaLicFin,'yyyy-MM-dd')");
					params.put("fechaLicFin", futListadoRequest.getFechaLicFin());
				}
							
				if(!StringUtils.isBlank(futListadoRequest.getCodigoOficina())) {
					queryBuilder.append(" AND A.COFI_CODOFI = :codigoOficina");
					params.put("codigoOficina", futListadoRequest.getCodigoOficina());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getCodigoEmpleado())) {
					queryBuilder.append(" AND A.CEMP_CODEMP = :codigoEmpleado");
					params.put("codigoEmpleado", futListadoRequest.getCodigoEmpleado());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getCodigoMotivo())) {
					queryBuilder.append(" AND A.CMOT_CODMOT = :codigoMotivo");
					params.put("codigoMotivo", futListadoRequest.getCodigoMotivo());
				}
				
				if(!StringUtils.isBlank(futListadoRequest.getCodigoEstado())) {
					queryBuilder.append(" AND A.CFUT_ESTADO = :codigoEstado");
					params.put("codigoEstado", futListadoRequest.getCodigoEstado());
				}
				
				queryBuilder.append(" ORDER BY A.DFEC_FUT DESC) X OFFSET :offset ROWS FETCH NEXT :next ROWS ONLY");
				return namedParameterJdbcTemplate.query(queryBuilder.toString(), params, BeanPropertyRowMapper.newInstance(Fut.class));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
	}
	
	
	/* TOTAL DE REGISTROS */
	@Override
	public Long totalListar(FutListadoRequest futListadoRequest) throws Exception {
		try {
			
			logger.info("Ingresando al query para contar el total de los registros de FUT");
			
			StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(A.NLIC_NUMSEC)\r\n"
					+ "FROM RH_PER_LICENCIAS A \r\n"
					+ "LEFT JOIN RH_PER_EMPLEADOS B ON A.CEMP_CODEMP = B.CEMP_CODEMP \r\n"
					+ "LEFT JOIN SI_MAE_OFICINAS C ON A.COFI_CODOFI = C.COFI_CODOFI \r\n"
					+ "LEFT JOIN RH_PER_MOTIVOLICE D ON A.CMOT_CODMOT = D.CMOT_CODMOT \r\n"
					+ "LEFT JOIN SI_MAE_ESTADOS E ON A.CFUT_ESTADO = E.CEST_CODEST \r\n"
					+ "WHERE E.CEST_NOMTAB='RH_PER_LICENCIAS'");
			
			
			Map<String, Object> params = new HashMap<String, Object>();
			
			if(!StringUtils.isBlank(futListadoRequest.getFechaFutInicio())) {
				queryBuilder.append( " AND A.DFEC_FUT >= TO_DATE(:fechaFutInicio,'yyyy-MM-dd')");
				params.put("fechaFutInicio", futListadoRequest.getFechaFutInicio());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getFechaFutFin())) {
				queryBuilder.append( " AND A.DFEC_FUT <= TO_DATE(:fechaFutFin,'yyyy-MM-dd')");
				params.put("fechaFutFin", futListadoRequest.getFechaFutFin());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getFechaLicInicio())) {
				queryBuilder.append( " AND A.FLIC_FECINI >= TO_DATE(:fechaLicInicio,'yyyy-MM-dd')");
				params.put("fechaLicInicio", futListadoRequest.getFechaLicInicio());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getFechaLicFin())) {
				queryBuilder.append( " AND A.FLIC_FECFIN <= TO_DATE(:fechaLicFin,'yyyy-MM-dd')");
				params.put("fechaLicFin", futListadoRequest.getFechaLicFin());
			}
						
			if(!StringUtils.isBlank(futListadoRequest.getCodigoOficina())) {
				queryBuilder.append(" AND A.COFI_CODOFI = :codigoOficina");
				params.put("codigoOficina", futListadoRequest.getCodigoOficina());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getCodigoEmpleado())) {
				queryBuilder.append(" AND A.CEMP_CODEMP = :codigoEmpleado");
				params.put("codigoEmpleado", futListadoRequest.getCodigoEmpleado());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getCodigoMotivo())) {
				queryBuilder.append(" AND A.CMOT_CODMOT = :codigoMotivo");
				params.put("codigoMotivo", futListadoRequest.getCodigoMotivo());
			}
			
			if(!StringUtils.isBlank(futListadoRequest.getCodigoEstado())) {
				queryBuilder.append(" AND A.CFUT_ESTADO = :codigoEstado");
				params.put("codigoEstado", futListadoRequest.getCodigoEstado());
			}
			
			queryBuilder.append(" ORDER BY A.DFEC_FUT DESC");
			return namedParameterJdbcTemplate.queryForObject(queryBuilder.toString(), params, Long.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}
	
	
	/*	Store Procedure REGISTRAR FUT  */
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String insertarSP(Fut fut) throws Exception {

		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_REGISTRAR_FUT") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_CODMOT", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_HORA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_HORA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_JUST", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_vID", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		    		.addValue("p_vV_FECHA", fut.getFfutFecsol())
				    .addValue("p_vV_CODOFI", fut.getCofiCodofi())
				    .addValue("p_vV_CEMP_CODEMP", fut.getCempCodemp())
				    .addValue("p_vV_CODMOT", fut.getCmotCodmot())
				    .addValue("p_vV_FECHA_INI", fut.getFlicFecini())
				    .addValue("p_vV_FECHA_FIN", fut.getFlicFecfin())
				    .addValue("p_vV_HORA_INI", fut.getFfutHorini())
				    .addValue("p_vV_HORA_FIN", fut.getFfutHorfin())
				    .addValue("p_vV_JUST", fut.getJust())
				    .addValue("p_vV_CODUSU", fut.getCusuUsureg())
		    		;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		
		    if(result.get("p_vID")==null) throw new Exception("Error al insertar cabecera: ID esperado nulo");
	    	return String.valueOf(result.get("p_vID"));
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al registrar el FUT.");
		}
	}

	
	/* OFICINAS */
	@Override
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		try {
			
			//logger.info("Ingresando al query para listar las oficinas");
			
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
					+ "AND B.COPE_CODOPE= 'OP008'\r\n"		//Código de FUT
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
	
	/* ESTADOS */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarEstados() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_ESTADOS_FUT") 
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
			throw new Exception("Error al listar estados");
		}
	}
	
	
	/* MOTIVOS */
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarMotivos() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_LISTAR_MOTIVOS_FUT") 
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
			throw new Exception("Error al listar motivos");
		}
	}
		
	/* EMPLEADOS X OFICINA */
	
	@Override
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception {
		try {
			
			//logger.info("Ingresando al query para listar los empleados por Oficinas");
			
			String sql = "SELECT A.CEMP_CODEMP, A.CEMP_APEPAT || ' ' || A.CEMP_APEMAT || ' ' || A.CEMP_PRINOM AS nombre,\r\n"
					+ "          COALESCE(B.CCAR_DESCAR, C.CDES_SERV) AS cargo \r\n"
					+ "          FROM RH_PER_EMPLEADOS A\r\n"
					+ "          LEFT JOIN RH_MAE_CARGOS B ON B.CCAR_CODCAR = A.CCAR_CODCAR\r\n"
					+ "          LEFT JOIN RH_MAE_SERVICIOS C ON C.CCOD_SERV = A.CCOD_SERV\r\n"
					+ "          WHERE COFI_CODOFI = ? \r\n"
					+ "          AND (CEMP_REGLAB = '01' OR CEMP_REGLAB = '02' OR CEMP_REGLAB = '04') \r\n"		/* CONDICIONAL PARA FILTRAR SÓLO 276,728 Y CAS */
					+ "          AND (CEMP_INDBAJ = '0' OR FEMP_FECBAJ >= SYSDATE)\r\n"
					+ "          ORDER BY CEMP_APEPAT";
			Object[] params = new Object[]{codigoOficina};
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Empleado.class), params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar los empleados por oficina -> " + codigoOficina);
		} 
	}
	
	@Override
	public Fut editarFut(String anio, String numeroSolicitud) throws Exception {
		try {
			String sql = " SELECT A.CANO_ANOEJE canoAnosol, TO_CHAR(A.DFEC_FUT,'YYYY-MM-DD') ffutFecsol, "
					+ "A.NLIC_NUMSEC numeroFut, A.CEMP_CODEMP cempCodemp, A.CFUT_ESTADO cfutEstado, NVL(A.CIND_AUT,'0') indFirmado, "
					+ "B.CEMP_APEPAT || ' ' || B.CEMP_APEMAT || ' ' || B.CEMP_PRINOM || ' ' || B.CEMP_SEGNOM nomEmpleado, "
					+ "COALESCE(F.CCAR_DESCAR, G.CDES_SERV) cargoEmpleado, "
					+ "A.COFI_CODOFI , C.COFI_NOMCOR oficina, "
					+ "TO_CHAR(A.FLIC_FECINI,'YYYY-MM-DD') flicFecini, "
					+ "(TO_CHAR(A.FLIC_HORINI, 'HH24:MI')) ffutHorini, "
					+ "TO_CHAR(A.FLIC_FECFIN,'YYYY-MM-DD') flicFecfin, "
					+ "(TO_CHAR(A.FLIC_HORFIN, 'HH24:MI')) ffutHorfin, "
					+ "TRUNC(A.FLIC_FECFIN) - TRUNC(A.FLIC_FECINI) + 1 AS NUMDIAS, "
					+ "TO_CHAR(A.DFEC_FUT), A.CMOT_CODMOT cmotCodmot, D.CMOT_DESMOT motivo, "
					+ "E.CEST_DESLAR estado, A.CLIC_OBSER just "
					+ "FROM RH_PER_LICENCIAS A  "
					+ "LEFT JOIN RH_PER_EMPLEADOS B "
					+ "ON A.CEMP_CODEMP = B.CEMP_CODEMP "
					+ "LEFT JOIN SI_MAE_OFICINAS C "
					+ "ON A.COFI_CODOFI = C.COFI_CODOFI "
					+ "LEFT JOIN RH_PER_MOTIVOLICE D "
					+ "ON A.CMOT_CODMOT = D.CMOT_CODMOT "
					+ "LEFT JOIN SI_MAE_ESTADOS E "
					+ "ON A.CFUT_ESTADO = E.CEST_CODEST "
					+ "LEFT JOIN RH_MAE_CARGOS F "
					+ "ON F.CCAR_CODCAR = B.CCAR_CODCAR "
					+ "LEFT JOIN RH_MAE_SERVICIOS G "
					+ "ON G.CCOD_SERV = B.CCOD_SERV "
					+ "WHERE E.CEST_NOMTAB='RH_PER_LICENCIAS' "
					+ "AND A.CANO_ANOEJE= ? AND A.NLIC_NUMSEC= ? ";
			
			Object[] params = new Object[]{anio,numeroSolicitud};
			
			Fut fut = jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Fut.class), params).stream().findFirst().orElse(null);
		
			return fut; 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar el FUT: " + anio + "-" + numeroSolicitud);
		}
	}
	
	
	/*
	 *  STORE PROCEDURE EDITAR
	 */	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void actualizarSP(Fut fut) throws Exception {
		
			try {
				SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
				        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_MODIFICAR_FUT") 
				        .withoutProcedureColumnMetaDataAccess()
				        .declareParameters(
				            new SqlParameter("p_vV_FECHA", Types.VARCHAR),
				            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
				            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
				            new SqlParameter("p_vV_CODMOT", Types.VARCHAR),
				            new SqlParameter("p_vV_FECHA_INI", Types.VARCHAR),
				            new SqlParameter("p_vV_FECHA_FIN", Types.VARCHAR),
				            new SqlParameter("p_vV_HORA_INI", Types.VARCHAR),
				            new SqlParameter("p_vV_HORA_FIN", Types.VARCHAR),
				            new SqlParameter("p_vV_JUST", Types.VARCHAR),
				            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
				            new SqlOutParameter("p_vID", Types.VARCHAR),
				            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
				            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
				        );

			    SqlParameterSource inParams = new MapSqlParameterSource()
			    		.addValue("p_vV_FECHA", fut.getFfutFecsol())
					    .addValue("p_vV_CODOFI", fut.getCofiCodofi())
					    .addValue("p_vV_CEMP_CODEMP", fut.getCempCodemp())
					    .addValue("p_vV_CODMOT", fut.getCmotCodmot())
					    .addValue("p_vV_FECHA_INI", fut.getFlicFecini())
					    .addValue("p_vV_FECHA_FIN", fut.getFlicFecfin())
					    .addValue("p_vV_HORA_INI", fut.getFfutHorini())
					    .addValue("p_vV_HORA_FIN", fut.getFfutHorfin())
					    .addValue("p_vV_JUST", fut.getJust())
					    .addValue("p_vV_CODUSU", fut.getCusuUsureg())
			    		;
			
			    Map<String, Object> result = jdbcCall.execute(inParams);
			    repositoryUtil.analizarResultado(result);
			} catch (ValidacionPersonalizadaException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new Exception("Error al actualizar el FUT");
			}
		}

	
	/*
	 *  STORE PROCEDURE APROBAR
	 */	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void aprobarSP(Fut fut) throws Exception {

		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_APROBAR_FUT") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_FECHA", Types.VARCHAR),
			            new SqlParameter("p_vV_CODOFI", Types.VARCHAR),
			            new SqlParameter("p_vV_CEMP_CODEMP", Types.VARCHAR),
			            new SqlParameter("p_vV_CODMOT", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_FECHA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_HORA_INI", Types.VARCHAR),
			            new SqlParameter("p_vV_HORA_FIN", Types.VARCHAR),
			            new SqlParameter("p_vV_JUST", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_vID", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		    		.addValue("p_vV_FECHA", fut.getFfutFecsol())
				    .addValue("p_vV_CODOFI", fut.getCofiCodofi())
				    .addValue("p_vV_CEMP_CODEMP", fut.getCempCodemp())
				    .addValue("p_vV_CODMOT", fut.getCmotCodmot())
				    .addValue("p_vV_FECHA_INI", fut.getFlicFecini())
				    .addValue("p_vV_FECHA_FIN", fut.getFlicFecfin())
				    .addValue("p_vV_HORA_INI", fut.getFfutHorini())
				    .addValue("p_vV_HORA_FIN", fut.getFfutHorfin())
				    .addValue("p_vV_JUST", fut.getJust())
				    .addValue("p_vV_CODUSU", fut.getCusuUsureg())
		    		;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al aprobar el FUT.");
		}
	}

	
	/*
	 *  STORE PROCEDURE ANULAR
	 */
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void anularSP(Fut fut) throws Exception {

		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ASISTENCIA.SIADW_SP_ANULAR_FUT") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_ANIO", Types.VARCHAR),
			            new SqlParameter("p_vV_NUMSOL", Types.VARCHAR),
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );
			
			String anio = fut.getCanoAnosol();
			String numsol = fut.getCsolNumsol();
			String usuario = fut.getCusuUsureg();
			logger.info("Valores: " + anio + " " + numsol + " " + usuario);

		    SqlParameterSource inParams = new MapSqlParameterSource()
		    		.addValue("p_vV_ANIO", fut.getCanoAnosol())
				    .addValue("p_vV_NUMSOL", fut.getCsolNumsol())
				    .addValue("p_vV_CODUSU", fut.getCusuUsureg())
				    ;
		
		    Map<String, Object> result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		} catch (ValidacionPersonalizadaException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al anular el FUT.");
		}
	}

		
	@Override
	public List<FlujoAprobacion> listarFlujoAprobacion(String anio, String numeroSolicitud) throws Exception {
		try {
			String sql = "SELECT B.CUSU_CODUSU codigoUsuario, C.CEST_DESCOR descripcionEstado, TO_CHAR(A.DFEC_CRE, 'DD/MM/YYYY') fechaEstado "
					+ "FROM SIGA.LG_ABA_PEDAUTOR A "
					+ "LEFT JOIN SIGA.SI_USUARIO B  "
					+ "ON A.CEMP_CODEMP = B.CEMP_CODEMP AND B.CEMP_REAL IS NULL AND B.DFEC_FECSAL IS NULL "
					+ "INNER JOIN SIGA.SI_MAE_ESTADOS C "
					+ "ON C.CEST_NOMTAB='RH_PER_LICENCIAS' AND A.CPED_CODEST = C.CEST_CODEST "
					+ "WHERE CPED_DESMOT = 'OP008' "
					+ "AND CANO_ANOEJE= ? "
					+ "AND CPED_NUMPED= ? "
					+ "ORDER BY CPED_CODEST ASC";
			Object[] params = new Object[]{anio,numeroSolicitud};
			return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(FlujoAprobacion.class), params);
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar el flujo de aprobación del FUT: " + anio + "-" + numeroSolicitud);
		}
	}
}
