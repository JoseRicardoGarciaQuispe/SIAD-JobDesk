package gob.presidencia.siad.repository.impl;

import java.sql.Types;
import java.util.ArrayList;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.repository.IUserRepository;
import gob.presidencia.siad.util.Constantes;
import gob.presidencia.siad.util.RepositoryUtil;

@Repository
public class UserRepositoryImpl implements IUserRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public UserDetails autenticarUsuarioSP(String usuario, String clave, String anioEjecucion) throws Exception {
		Map<String, Object> result = null;
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_AUTENTICACION.SIADW_SP_AUTENTICAR_USUARIO") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
			            new SqlParameter("p_vV_CODUSU", Types.VARCHAR),
			            new SqlParameter("p_vV_CLAVE", Types.VARCHAR),
			            new SqlOutParameter("p_vFLAG_INTENTO", Types.VARCHAR),
			            new SqlOutParameter("p_vCODEMP", Types.VARCHAR),
			            new SqlOutParameter("p_vNOMEMP", Types.VARCHAR),
			            new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
			            new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR) 
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
		        .addValue("p_vV_CODUSU", usuario)
		        .addValue("p_vV_CLAVE", clave)
		        ;
		
		    result = jdbcCall.execute(inParams);
		    repositoryUtil.analizarResultado(result);
		    
		    if(result.get("p_vCODEMP")==null || result.get("p_vNOMEMP")==null) throw new Exception("Respuesta esperada nula");
		    
		    String nombres = String.valueOf(result.get("p_vNOMEMP"));
		    String codigoEmpleado = String.valueOf(result.get("p_vCODEMP"));
		    
		    List<GrantedAuthority> authorities = new ArrayList<>();
		    UserDetails userDetails = new UserDetails(usuario, "", authorities);
			userDetails.setNombres(nombres);
			userDetails.setAnio(anioEjecucion);
			userDetails.setCodigoEmpleado(codigoEmpleado);
			userDetails.setCodinstitucion(Constantes.CODIGO_INSTITUCION);
			userDetails.setPeriodo(Constantes.PERIODO_EJECUCION);
			
			return userDetails;
		} catch (ValidacionPersonalizadaException e) {
			if(result.get("p_vFLAG_INTENTO")!=null && String.valueOf(result.get("p_vFLAG_INTENTO")).equals("1")) {
				logger.info("Se incrementa los intentos..");
			}
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error en la autenticacion del sistema");
		}
	}

	@Override
	public void asignarAnioSP(String anio) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.PK_VARGLOBAL.asigna_anio") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("avalor", Types.VARCHAR)
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("avalor", anio)
		        ;
		
		    jdbcCall.execute(inParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al asignar el año");
		}
	}

	@Override
	public void asignarEjecucionSP(String ejecucion) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.PK_VARGLOBAL.asigna_ejec") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("evalor", Types.VARCHAR)
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("evalor", ejecucion)
		        ;
		
		    jdbcCall.execute(inParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al asignar la ejecucion");
		}
	}

	@Override
	public void asignarPeriodoSP(String periodo) throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.PK_VARGLOBAL.asigna_periodo") 
			        .withoutProcedureColumnMetaDataAccess()
			        .declareParameters(
		        		new SqlParameter("pvalor", Types.VARCHAR)
			        );

		    SqlParameterSource inParams = new MapSqlParameterSource()
	    		.addValue("pvalor", periodo)
		        ;
		
		    jdbcCall.execute(inParams);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al asignar el periodo");
		}
	}
	
	@Override
	public void mostrarVariables() throws Exception {
        String anio = jdbcTemplate.queryForObject(
            "SELECT PK_VARGLOBAL.retorna_anio() FROM DUAL", String.class);
        String codinstitucion = jdbcTemplate.queryForObject(
                "SELECT PK_VARGLOBAL.retorna_ejec() FROM DUAL", String.class);
        String periodo = jdbcTemplate.queryForObject(
                "SELECT PK_VARGLOBAL.retorna_periodo() FROM DUAL", String.class);
        logger.info("ANIO -> " + anio);
        logger.info("INSTITUCION -> " + codinstitucion);
        logger.info("PERIODO -> " + periodo);
    }

}
