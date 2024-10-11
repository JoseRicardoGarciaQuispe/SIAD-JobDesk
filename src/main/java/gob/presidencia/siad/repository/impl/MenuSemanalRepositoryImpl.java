package gob.presidencia.siad.repository.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.MenuSemanal;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Semana;
import gob.presidencia.siad.repository.IMenuSemanalRepository;
import gob.presidencia.siad.util.RepositoryUtil;
import oracle.jdbc.OracleTypes;

@Repository
public class MenuSemanalRepositoryImpl implements IMenuSemanalRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(MenuSemanalRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
		
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarAniosSP() throws Exception {
		try {
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
			        .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LISTAR_ANIOS_MENU_SEM") 
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
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Semana> listarSemanaSP(String anio) throws Exception {
	    try {
	        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LIST_SEMANA_MENU_SEM") 
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                new SqlParameter("p_vV_ANIO", Types.VARCHAR), 
	                new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
	                new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
	                new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR)
	            );

	        Map<String, Object> inParams = new HashMap<>();
	        inParams.put("p_vV_ANIO", anio);

	        Map<String, Object> result = jdbcCall.execute(inParams);
	        repositoryUtil.analizarResultado(result);

	        if (result.get("p_cLISTADO") == null) throw new Exception("Respuesta esperada nula");
	        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
	        return repositoryUtil.castToList(Semana.class, list);
	    } catch (ValidacionPersonalizadaException e) {
	        throw e;
	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        throw new Exception("Error al listar semanas");
	    }
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<Parametro> listarDiasSP(String anio, String numSemana) throws Exception {
	    try {
	        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LIST_DIAS_SEM_MENU") 
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                new SqlParameter("p_vV_ANIO", Types.VARCHAR), 
	                new SqlParameter("p_nNUM_SEMANA", Types.INTEGER),
	                new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
	                new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
	                new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR)
	            );

	        Map<String, Object> inParams = new HashMap<>();
	        inParams.put("p_vV_ANIO", anio);
	        inParams.put("p_nNUM_SEMANA", numSemana);

	        Map<String, Object> result = jdbcCall.execute(inParams);
	        repositoryUtil.analizarResultado(result);

	        if (result.get("p_cLISTADO") == null) throw new Exception("Respuesta esperada nula");
	        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");
	        
	        List<Parametro> parametros = repositoryUtil.castToList(Parametro.class, list);
	        
// 			Imprimir los valores devueltos en el cursor p_cLISTADO
//	        for (Parametro parametro : parametros) {
//	            logger.info("Valor devuelto: {}", parametro.toString());
//	        }
	        
	        return parametros;
	        
	        //return repositoryUtil.castToList(Parametro.class, list);
	    } catch (ValidacionPersonalizadaException e) {
	        throw e;
	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        throw new Exception("Error al listar dias");
	    }
	}
	


	@SuppressWarnings("unchecked")
	@Override
	public List<MenuSemanal> listarMenuSemanalSP(String anio, String numSemana, String fecha) throws Exception {
		try {
	        SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("SIGA.SIADW_PKG_ALIMENTACION.SIADW_SP_LIST_ALIMENTOS_MENU") 
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                new SqlParameter("p_vV_ANIO", Types.VARCHAR), 
	                new SqlParameter("p_nNUM_SEMANA", Types.INTEGER),
	                new SqlParameter("p_vV_FECHA", Types.VARCHAR),
	                new SqlOutParameter("p_nFLAG_RESULTADO", Types.INTEGER),
	                new SqlOutParameter("p_vMENSAJE_RESULTADO", Types.VARCHAR),
	                new SqlOutParameter("p_cLISTADO", OracleTypes.CURSOR)
	            );

	        Map<String, Object> inParams = new HashMap<>();
	        inParams.put("p_vV_ANIO", anio);
	        inParams.put("p_nNUM_SEMANA", numSemana);
	        inParams.put("p_vV_FECHA", fecha);

	        Map<String, Object> result = jdbcCall.execute(inParams);
	        repositoryUtil.analizarResultado(result);
	        
	        if (result.get("p_cLISTADO") == null) throw new Exception("Respuesta esperada nula");
	        List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("p_cLISTADO");       
      	         
	        List<MenuSemanal> menuSemanal = repositoryUtil.castToList(MenuSemanal.class, list);
	        
 			//Imprimir los valores devueltos en el cursor p_cLISTADO
	        //for (MenuSemanal menu : menuSemanal) {
	        //    logger.info("Valor devuelto: {}", menu.toString());
	        //}
	        	        
	        //return repositoryUtil.castToList(MenuSemanal.class, list);
	        return menuSemanal;
	    } catch (ValidacionPersonalizadaException e) {
	        throw e;
	    } catch (Exception e) {
	        logger.error(e.getMessage(), e);
	        throw new Exception("Error al listar el menú semanal");
	    }
	}	
}
