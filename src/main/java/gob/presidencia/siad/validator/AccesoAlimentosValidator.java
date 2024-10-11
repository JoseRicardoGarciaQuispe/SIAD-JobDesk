package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;

@Component
public class AccesoAlimentosValidator {
	
	public List<String> validateSearch(AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(accesoAlimentosListadoRequest.getFecha())) {
				errors.add("La fecha es obligatoria");
		    }
			if(StringUtils.isBlank(accesoAlimentosListadoRequest.getCodigoOficina())) {
				errors.add("La oficina es obligatoria");
		    }
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
}
