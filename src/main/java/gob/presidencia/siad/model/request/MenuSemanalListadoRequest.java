package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class MenuSemanalListadoRequest {
	
	//Filtros
	private String anio;
	private String numSemana;
	
	/*
	 * Campos para paginación del datatable
	 */
	private int draw;
	private int start;
	private int length;
	
}
