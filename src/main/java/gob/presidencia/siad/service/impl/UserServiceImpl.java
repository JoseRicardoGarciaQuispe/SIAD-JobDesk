package gob.presidencia.siad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gob.presidencia.siad.model.security.UserDetails;
import gob.presidencia.siad.repository.IUserRepository;
import gob.presidencia.siad.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private IUserRepository userRepository;

	@Override
	public UserDetails autenticarUsuario(String usuario, String clave, String anioEjecucion) throws Exception {
		return userRepository.autenticarUsuarioSP(usuario, clave, anioEjecucion);
	}

	@Override
	public void asignarVariablesGlobales(String anio, String ejecucion, String periodo) throws Exception {
		userRepository.asignarAnioSP(anio);
		userRepository.asignarEjecucionSP(ejecucion);
		userRepository.asignarPeriodoSP(periodo);
	}

	@Override
	public void mostrarVariablesGlobales() throws Exception {
		userRepository.mostrarVariables();
	}

}
