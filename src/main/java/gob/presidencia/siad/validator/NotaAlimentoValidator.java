package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.NotaAlimentoDetalle;

@Component
public class NotaAlimentoValidator {
		
	public List<String> validateCreate(NotaAlimento notaAlimento) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(notaAlimento.getDsolFecsol())) {
				errors.add("La fecha de la nota es obligatoria");
		    }
			if(StringUtils.isBlank(notaAlimento.getDsolFecini())) {
				errors.add("La fecha de inicio es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getDsolFecfin())) {
		    	errors.add("La fecha de fin es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getCofiCodofi())) {
				errors.add("La oficina es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getCsolIndapo())) {
		    	errors.add("El indicador de apoyo es obligatorio");
		    }
		    if(notaAlimento.getItems()==null || notaAlimento.getItems().isEmpty()) {
				errors.add("El detalle de la nota es obligatorio");
			}else {
				for(NotaAlimentoDetalle notaAlimentoDetalle : notaAlimento.getItems()) {
					if(StringUtils.isBlank(notaAlimentoDetalle.getChorCodtipo())) {
						errors.add("El tipo de racion en el detalle es obligatorio");
					}
					if(notaAlimento.getCsolIndapo()!=null && notaAlimento.getCsolIndapo().equals("1")) {
						if(StringUtils.isBlank(notaAlimentoDetalle.getCapoCodapo())) {
							errors.add("El código de apoyo en el detalle es obligatorio");
						}
					}else {
						if(StringUtils.isBlank(notaAlimentoDetalle.getCempCodemp())) {
							errors.add("El código de empleado en el detalle es obligatorio");
						}
					}
					if(notaAlimentoDetalle.getNsodCanper()<1) {
						errors.add("La cantidad en el detalle es incorrecta");
					}
				}
			}
		    
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> validateUpdate(NotaAlimento notaAlimento) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(notaAlimento.getDsolFecsol())) {
				errors.add("La fecha de la nota es obligatoria");
		    }
			if(StringUtils.isBlank(notaAlimento.getDsolFecini())) {
				errors.add("La fecha de inicio es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getDsolFecfin())) {
		    	errors.add("La fecha de fin es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getCofiCodofi())) {
				errors.add("La oficina es obligatoria");
		    }
		    if(StringUtils.isBlank(notaAlimento.getCsolIndapo())) {
		    	errors.add("El indicador de apoyo es obligatorio");
		    }		    
		    if(notaAlimento.getListaAgregar()!=null && !notaAlimento.getListaAgregar().isEmpty()) {
		    	for(NotaAlimentoDetalle notaAlimentoDetalle : notaAlimento.getListaAgregar()) {
		    		if(StringUtils.isBlank(notaAlimentoDetalle.getChorCodtipo())) {
						errors.add("El tipo de racion en el detalle es obligatorio");
					}
		    		if(notaAlimento.getCsolIndapo()!=null && notaAlimento.getCsolIndapo().equals("1")) {
		    			if(StringUtils.isBlank(notaAlimentoDetalle.getCapoCodapo())) {
							errors.add("El código de apoyo en el detalle es obligatorio");
						}
		    		}else {
		    			if(StringUtils.isBlank(notaAlimentoDetalle.getCempCodemp())) {
							errors.add("El código de empleado en el detalle es obligatorio");
						}
		    		}
					if(notaAlimentoDetalle.getNsodCanper()<1) {
						errors.add("La cantidad en el detalle es incorrecta");
					}
				}
		    }
		    
		    if(notaAlimento.getListaModificar()!=null && !notaAlimento.getListaModificar().isEmpty()) {
		    	for(NotaAlimentoDetalle notaAlimentoDetalle : notaAlimento.getListaModificar()) {
		    		if(notaAlimentoDetalle.getNsodItem()==0){
		    			errors.add("El correlativo de racion en el detalle es obligatorio");
		    		}
		    		if(StringUtils.isBlank(notaAlimentoDetalle.getChorCodtipo())) {
						errors.add("El tipo de racion en el detalle es obligatorio");
					}
		    		if(notaAlimento.getCsolIndapo()!=null && notaAlimento.getCsolIndapo().equals("1")) {
		    			if(StringUtils.isBlank(notaAlimentoDetalle.getCapoCodapo())) {
							errors.add("El código de apoyo en el detalle es obligatorio");
						}
		    		}else {
		    			if(StringUtils.isBlank(notaAlimentoDetalle.getCempCodemp())) {
							errors.add("El código de empleado en el detalle es obligatorio");
						}
		    		}
					if(notaAlimentoDetalle.getNsodCanper()<1) {
						errors.add("La cantidad en el detalle es incorrecta");
					}
				}
		    }
		    
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> validatePK(NotaAlimento notaAlimento) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(notaAlimento.getCanoAnosol())) {
				errors.add("El año se encuentra nulo o vacío");
		    }
			if(StringUtils.isBlank(notaAlimento.getCdocTipdoc())) {
				errors.add("El tipo de documento se encuentra nulo o vacío");
		    }
		    if(StringUtils.isBlank(notaAlimento.getCsolNumsol())) {
		    	errors.add("El número de solicitud se encuentra nulo o vacío");
		    }
		    
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
	
}
