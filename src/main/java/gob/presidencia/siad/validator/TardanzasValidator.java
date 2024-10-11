package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.model.request.TardanzasListadoRequest;

@Component
public class TardanzasValidator {
		
	public List<String> validateSearch(TardanzasListadoRequest tardanzasListadoRequest) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(tardanzasListadoRequest.getFechaInicio())) {
				errors.add("La fecha de inicio es obligatoria");
		    }
			if(StringUtils.isBlank(tardanzasListadoRequest.getFechaFin())) {
		    	errors.add("La fecha de fin es obligatoria");
		    }
		    
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}

}
