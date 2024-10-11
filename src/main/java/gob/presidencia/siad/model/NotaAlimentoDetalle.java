package gob.presidencia.siad.model;

import lombok.Data;

@Data
public class NotaAlimentoDetalle {

	private String canoAnosol;
	private String cdocTipdoc;
	private String csolNumsol;
	private int nsodItem;
	private String chorCodtipo;
	private String capoCodapo;
	private String cempCodemp;
	private String cofiCodofi;
	private int nsodCanper;
	private String csodSodobs;
	private String cusuUsureg;
	private String cusuUsumod;
	
	/*
	 *  Campos mostrados en la tabla
	 */
	private String fotocheck;
	private String descripcionRacion;
	private String nombres;
	
}
