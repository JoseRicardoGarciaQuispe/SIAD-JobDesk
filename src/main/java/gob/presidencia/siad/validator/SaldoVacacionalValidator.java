package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import gob.presidencia.siad.model.request.SaldoVacacionalListadoRequest;

@Component
public class SaldoVacacionalValidator {
	
	public List<String> validateSearch(SaldoVacacionalListadoRequest saldoVacacionalListadoRequest) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(saldoVacacionalListadoRequest.getCodigoOficina())) {
				errors.add("La oficina es obligatoria");
		    }
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}
}
