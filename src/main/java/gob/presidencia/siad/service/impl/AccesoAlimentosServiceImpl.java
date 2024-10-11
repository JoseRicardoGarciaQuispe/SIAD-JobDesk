package gob.presidencia.siad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.AccesoAlimentos;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;
import gob.presidencia.siad.repository.IAccesoAlimentosRepository;
import gob.presidencia.siad.service.IAccesoAlimentosService;

@Service
public class AccesoAlimentosServiceImpl implements IAccesoAlimentosService {
	
	@Autowired
	private IAccesoAlimentosRepository accesoAlimentosRepository;
	
	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		return accesoAlimentosRepository.listarOficinasSP(anio, codigoUsuario);
	}

	@Override
	public DataTable<AccesoAlimentos> listarSP(int pageNo, int pageSize, AccesoAlimentosListadoRequest accesoAlimentosRequest) throws Exception {
		return accesoAlimentosRepository.listarSP(pageNo, pageSize, accesoAlimentosRequest);
	}

	@Override
	public List<AccesoAlimentos> listarAllSP(AccesoAlimentosListadoRequest accesoAlimentosRequest) throws Exception {
		return accesoAlimentosRepository.listarAllSP(accesoAlimentosRequest);
	}
}
