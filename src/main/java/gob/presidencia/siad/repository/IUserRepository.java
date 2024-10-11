package gob.presidencia.siad.repository;

import gob.presidencia.siad.model.security.UserDetails;

public interface IUserRepository {

	public UserDetails autenticarUsuarioSP(String usuario, String clave, String anioEjecucion) throws Exception;
	public void asignarAnioSP(String anio) throws Exception;
	public void asignarEjecucionSP(String ejecucion) throws Exception;
	public void asignarPeriodoSP(String periodo) throws Exception;
	public void mostrarVariables() throws Exception;
}
