package gob.presidencia.siad.model;

import lombok.Data;

@Data
public class Fut {
	
	//Campos del Fut a registrar
	
	private String canoAnosol;
	private String csolNumsol;
	private String ffutFecsol;
	private String cofiCodofi;
	private String cempCodemp;
	private String cmotCodmot;
	private String ffutHorini;	
	private String ffutHorfin;	
	private String flicFecini;
	private String flicFecfin;
	private String cfutEstado;
	private String just;
	private String cusuUsureg;
	private String cusuUsumod;
	private String indFirmado;
		
	
	//Campos mostrados en la tabla (query)
	
	private String numeroFut;
	private String nomEmpleado;
	private String cargoEmpleado;
	private String nombreOficina;
	private String fechaInicioFut;
	private String fechaFinFut;
	private int numeroDias;
	private String fechaSolicitudFut;
	private String motivo;
	private String estado;
	

}
