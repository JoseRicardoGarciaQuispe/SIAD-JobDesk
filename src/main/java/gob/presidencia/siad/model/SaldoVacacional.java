package gob.presidencia.siad.model;

import lombok.Data;

@Data
public class SaldoVacacional {
	/*
	 *  Campos mostrados en datatable saldo vacacional
	 */
	private String CODEMPLEADO;
	private String APENOMEMPLEADO;
	private String FECHAINGRESO;
	private String NOMOFICINA;
	private String PENDANIOSANT;
	private String PENDANIOACT;
	private String MESPROGRAMADO;
	private String TOTALPENDIENTE;	
}
