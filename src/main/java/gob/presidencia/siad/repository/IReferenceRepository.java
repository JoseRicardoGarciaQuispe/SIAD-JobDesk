package gob.presidencia.siad.repository;

public interface IReferenceRepository {

	public String validateSession(String codSistema, String ticket, String usuario) throws Exception;
	public String getUrlSistemaDefault(String codTabla, String codValor) throws Exception;
	
}
