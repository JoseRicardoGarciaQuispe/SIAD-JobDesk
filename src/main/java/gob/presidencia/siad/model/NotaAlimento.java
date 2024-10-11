package gob.presidencia.siad.model;

import java.util.List;

import lombok.Data;

@Data
public class NotaAlimento {

	private String canoAnosol;
	private String cdocTipdoc;
	private String csolNumsol;
	private String dsolFecsol;
	private String cofiCodofi;
	private String csolObssol;
	private String dsolFecini;
	private String dsolFecfin;
	private String cusuUsureg;
	private String cusuUsumod;
	private String csolEstado;
	private String csolIndapo;
	private String ssolMotivo;
	private String csolProce;
	
	private List<NotaAlimentoDetalle> items;
	private List<NotaAlimentoDetalle> listaAgregar;
	private List<NotaAlimentoDetalle> listaModificar;
	private List<NotaAlimentoDetalle> listaEliminar;
	
	/*
	 *  Campos mostrados en la tabla
	 */
	private String tipoDocumento;
	private String numeroSolicitud;
	private String fechaSolicitud;
	private String tipoSolicitud;
	private String nombreOficina;
	private String fechaInicioSolicitud;
	private String fechaFinSolicitud;
	private String estado;
	
}
