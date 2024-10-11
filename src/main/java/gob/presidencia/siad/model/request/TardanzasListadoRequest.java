
package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class TardanzasListadoRequest {
	
	private String codigoEmpleado;
	private String fechaInicio;
	private String fechaFin;
	private String cempCodemp;
	/*
	 * Campos para paginacion del datatable
	 */
	private int draw;
	private int start;
	private int length;
}
