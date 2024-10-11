package gob.presidencia.siad.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Tardanzas;
import gob.presidencia.siad.model.request.TardanzasListadoRequest;
import gob.presidencia.siad.repository.ITardanzasRepository;
import gob.presidencia.siad.service.ITardanzasService;

@Service
public class TardanzasServiceImpl implements ITardanzasService {
	
	@Autowired
	private ITardanzasRepository tardanzasRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Tardanzas> listar(int pageNo, int pageSize, TardanzasListadoRequest tardanzasRequest) throws Exception {
		return tardanzasRepository.listar(pageNo, pageSize, tardanzasRequest);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long totalListar(TardanzasListadoRequest tardanzasRequest) throws Exception {
		return tardanzasRepository.totalListar(tardanzasRequest);
	}

	@Override
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		return tardanzasRepository.listarOficinasSP(anio, codigoUsuario);
	}

	@Override
	public List<Parametro> listarEmpleadosPorOficina(String anio, String codigoUsuario, String codigoOficina) throws Exception {
		return tardanzasRepository.listarEmpleadosPorOficinaSP(anio, codigoUsuario, codigoOficina);
	}

}
