package gob.presidencia.siad.model;

import lombok.Data;

@Data
public class FlujoAprobacion {

	private String canoAnoeje;
	private String cestNomtab;
	private String nlicNumsec;

	
	/*
	 *  Campos para mostrar en vista
	 */
	
	private String codigoUsuario;
	private String descripcionEstado;
	private String fechaEstado;
	
}
