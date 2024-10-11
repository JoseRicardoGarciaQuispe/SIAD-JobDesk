package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class NotaAlimentoListadoRequest {

	private String fechaNotaInicio;
	private String fechaNotaFin;
	private String codigoOficina;
	private String codigoTipoDocumento;
	private String codigoTipoNota;
	private String codigoEstado;
	
	/*
	 * Campos para paginacion del datatable
	 */
	private int draw;
	private int start;
	private int length;
}
