package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class FutListadoRequest {
	
	//Filtros
	private String fechaFutInicio;
	private String fechaFutFin;
	private String codigoOficina;
	private String fechaLicInicio;
	private String fechaLicFin;
	private String codigoEmpleado;
	private String codigoMotivo;
	private String codigoEstado;
	
	/*
	 * Campos para paginación del datatable
	 */
	private int draw;
	private int start;
	private int length;
	
}
