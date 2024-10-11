package gob.presidencia.siad.model.request;

import lombok.Data;

@Data 
public class AccesoAlimentosListadoRequest {
	private String fecha;
	private String codigoOficina;
	private String empleado;
	private String estado;
	/*
	 * Campos para paginacion del datatable
	 */
	private int draw;
	private int start;
	private int length; 
}
