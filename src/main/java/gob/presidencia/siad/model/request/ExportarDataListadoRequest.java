package gob.presidencia.siad.model.request;

import lombok.Data;

@Data 
public class ExportarDataListadoRequest {
	
	private String descripcion;
	private String usuario;
	
	/*
	 * Campos para paginacion del datatable
	 */
	
	private int draw;
	private int start;
	private int length; 
}
