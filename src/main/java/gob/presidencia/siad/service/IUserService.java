package gob.presidencia.siad.service;
import gob.presidencia.siad.model.security.UserDetails;

public interface IUserService {

	public UserDetails autenticarUsuario(String codigoUsuario, String clave, String anioEjecucion) throws Exception;
	public void asignarVariablesGlobales(String anio, String ejecucion, String periodo) throws Exception;
	public void mostrarVariablesGlobales() throws Exception;
}
