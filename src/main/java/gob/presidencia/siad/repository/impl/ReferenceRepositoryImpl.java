package gob.presidencia.siad.repository.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import gob.presidencia.siad.exception.LoginException;
import gob.presidencia.siad.repository.IReferenceRepository;


@Repository
public class ReferenceRepositoryImpl implements IReferenceRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(ReferenceRepositoryImpl.class);

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public String validateSession(String codSistema, String ticket, String usuario) throws Exception {
		try {
			String sqlNumberSessions = "SELECT COUNT(1) FROM SIGA.SI_USUARIO_WEB U WHERE U.NSIS_NUMSIS = ? AND U.CTIC_ACCESO=? AND CUSU_CODUSU = ?";
			Object[] params = new Object[]{Long.parseLong(codSistema), ticket, usuario};
			int number = jdbcTemplate.queryForObject(sqlNumberSessions, Integer.class, params);
			if(number == 0) {
				throw new LoginException("El usuario " + usuario + " no tiene registrado una sesion");
			}else if(number > 1) {
				throw new LoginException("El usuario " + usuario + " no tiene una sesion valida");
			}else {
				String sql = "SELECT initcap(cusu_desusu)||'*'||u.cusu_codusu||'*'||u.cemp_codemp FROM SIGA.SI_USUARIO U WHERE "
						+ "U.CUSU_CODUSU IN (SELECT U.CUSU_CODUSU FROM  SIGA.SI_USUARIO_WEB U WHERE "
						+ "U.NSIS_NUMSIS = ? AND U.CTIC_ACCESO = ? AND CUSU_CODUSU = ?)";
				Object[] paramsSession = new Object[]{codSistema, ticket, usuario};
				String resultSession = jdbcTemplate.queryForObject(sql, String.class, paramsSession);
				if(StringUtils.isBlank(resultSession)) throw new LoginException("El usuario " + usuario + " no tiene registrada una sesion al querer obtenerla");
				return resultSession;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public String getUrlSistemaDefault(String codTabla, String codValor) throws Exception {
		try {
			String sql = "SELECT CELE_DESELE FROM SIGA.SI_ELEMENTO where CTAB_CODTAB = ? AND CELE_CODELE = ?";
			Object[] params = new Object[]{codTabla, codValor};
			return jdbcTemplate.queryForObject(sql, String.class, params);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Error al buscar la url por defecto");
		}
	}
	
}
