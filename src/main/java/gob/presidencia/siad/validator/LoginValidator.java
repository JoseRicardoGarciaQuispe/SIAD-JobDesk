package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.model.request.LoginRequest;

@Component
public class LoginValidator {
		
	public List<String> validateLogin(LoginRequest loginRequest) throws Exception {
		List<String> errors = new ArrayList<>();
		try {
			if(StringUtils.isBlank(loginRequest.getUsuario())) {
				errors.add("El usuario es obligatorio");
		    }
			if(StringUtils.isBlank(loginRequest.getClave())) {
				errors.add("La clave es obligatoria");
		    }	
//			if(StringUtils.isBlank(loginRequest.getAnioEjecucion())) {
//				errors.add("El año de ejecución es obligatorio");
//		    }	
		    return errors;
		} catch (Exception e) {
			throw e;
		}
	}

}
