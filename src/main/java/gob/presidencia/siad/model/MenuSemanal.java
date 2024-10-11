package gob.presidencia.siad.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class MenuSemanal {

	private String canoanoeje;
	private String nsemnumsem;
	private String ffecdiasem;
	private String chorcodtipo;
	
	/*
	 *  Campos mostrados en la tabla
	 */

	private String plato;
	
	@Getter(AccessLevel.NONE)
	private String lipidos;
	
	@Getter(AccessLevel.NONE)
	private String carbohidratos;
	
	@Getter(AccessLevel.NONE)
	private String calorias;
	
	@Getter(AccessLevel.NONE)
	private String proteinas;

	public String getLipidos() {
		if(StringUtils.isBlank(lipidos)) return null;
		return new BigDecimal(lipidos).setScale(1, RoundingMode.HALF_UP).toString();
	}

	public String getCarbohidratos() {
		if(StringUtils.isBlank(carbohidratos)) return null;
		return new BigDecimal(carbohidratos).setScale(1, RoundingMode.HALF_UP).toString();
	}

	public String getCalorias() {
		if(StringUtils.isBlank(calorias)) return null;
		return new BigDecimal(calorias).setScale(1, RoundingMode.HALF_UP).toString();
	}

	public String getProteinas() {
		if(StringUtils.isBlank(proteinas)) return null;
		return new BigDecimal(proteinas).setScale(1, RoundingMode.HALF_UP).toString();
	}
	
}
