package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.model.Fut;

@Component
public class FutValidator {
		
	public List<String> validateCreate(Fut fut) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(fut.getFfutFecsol())) {
				errors.add("La fecha de solicitud es obligatoria");
		    }
			if(StringUtils.isBlank(fut.getFlicFecini())) {
				errors.add("La fecha de inicio es obligatoria");
		    }
		    if(StringUtils.isBlank(fut.getFlicFecfin())) {
		    	errors.add("La fecha de fin es obligatoria");
		    }
		    if(StringUtils.isBlank(fut.getFfutHorini())) {
				errors.add("La hora de inicio es obligatoria");
		    }
		    if(StringUtils.isBlank(fut.getFfutHorfin())) {
		    	errors.add("La hora de término es obligatoria");
		    }
		    if(StringUtils.isBlank(fut.getCofiCodofi())) {
				errors.add("La oficina es obligatoria");
		    }
		    if(StringUtils.isBlank(fut.getCmotCodmot())) {
				errors.add("El motivo es obligatorio");
		    }
		    if(StringUtils.isBlank(fut.getJust())) {
				errors.add("La justificación es obligatoria");
		    }
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> validatePK(Fut fut) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(fut.getCanoAnosol())) {
				errors.add("El año se encuentra nulo o vacío");
		    }
			if(StringUtils.isBlank(fut.getCsolNumsol())) {
		    	errors.add("El número de solicitud se encuentra nulo o vacío");
		    }
		    
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
}
