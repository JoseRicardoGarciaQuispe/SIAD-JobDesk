package gob.presidencia.siad.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class FileDocument implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte[] archivo;
	private String nombre;

}
