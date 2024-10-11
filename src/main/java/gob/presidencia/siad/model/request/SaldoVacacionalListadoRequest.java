package gob.presidencia.siad.model.request;

import lombok.Data;

@Data 
public class SaldoVacacionalListadoRequest {
	private String anio;//Estos datos ya son obtenidos al logearse
	private String codigoOficina;//Estos datos ya son obtenidos al logearse
	private String empleado;//Este dato es obtenido desde inputBox
	private String codigoUsuario;//Estos datos ya son obtenidos al logearse
	private String codigoEmpleado;//Estos datos ya son obtenidos al logearse
	
	
	/*
	 * Campos para paginacion del datatable
	 */
	private int draw;
	private int start;
	private int length; 
}
