package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class LoginRequest {

	private String usuario;
	private String clave;
	private String anioEjecucion;
	
}
