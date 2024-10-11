package gob.presidencia.siad.model;

import lombok.Data;

/*
 *  CLASE GENERICA USADA PARA MAPEAR COLUMNAS DE CODIGO Y DESCRIPCION
 */
@Data
public class Parametro {

	private String codigo;
	private String nombre;
	
	private String descripcion;
	private String descripcionA;
}
