package gob.presidencia.siad.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gob.presidencia.siad.repository.IReferenceRepository;
import gob.presidencia.siad.service.IReferenceService;


@Service
public class ReferenceServiceImpl implements IReferenceService {
	
	@Autowired
	private IReferenceRepository referenceRepository;

	@Override
	public String validateSession(String codSistema, String ticket, String usuario) throws Exception {
		return referenceRepository.validateSession(codSistema, ticket, usuario);
	}

	@Override
	public String getUrlSistemaDefault(String codTabla, String codValor) throws Exception {
		return referenceRepository.getUrlSistemaDefault(codTabla, codValor);
	}

}
