package gob.presidencia.siad.service;

public interface IReferenceService {
	
	public String validateSession(String codSistema, String ticket, String usuario) throws Exception;
	public String getUrlSistemaDefault(String codTabla, String codValor) throws Exception;
	
}
