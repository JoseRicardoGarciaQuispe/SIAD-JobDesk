package gob.presidencia.siad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.MenuSemanal;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Semana;
import gob.presidencia.siad.repository.IMenuSemanalRepository;
import gob.presidencia.siad.service.IMenuSemanalService;

@Service
public class MenuSemanalServiceImpl implements IMenuSemanalService {
	
	@Autowired
	private IMenuSemanalRepository menuSemanalRepository;

		
	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarAnios() throws Exception {
		return menuSemanalRepository.listarAniosSP();
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Semana> listarSemanas(String anio) throws Exception {
		return menuSemanalRepository.listarSemanaSP(anio);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarDias(String anio, String numSemana) throws Exception {
		return menuSemanalRepository.listarDiasSP(anio, numSemana);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<MenuSemanal> listarMenuSemanal(String anio, String numSemana, String fecha) throws Exception{
		return menuSemanalRepository.listarMenuSemanalSP(anio, numSemana, fecha);
	}

}
