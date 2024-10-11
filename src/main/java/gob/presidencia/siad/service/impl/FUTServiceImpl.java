package gob.presidencia.siad.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoAprobacion;
import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.FutListadoRequest;
import gob.presidencia.siad.repository.IFUTRepository;
import gob.presidencia.siad.service.IFutService;

@Service
public class FUTServiceImpl implements IFutService {
	
	@Autowired
	private IFUTRepository futRepository;

	@Override
	@Transactional(readOnly = true)
	public List<Fut> listar(int pageNo, int pageSize, FutListadoRequest futRequest) throws Exception {
		if(StringUtils.isBlank(futRequest.getCodigoOficina())) return new ArrayList<>();
		return futRepository.listar(pageNo, pageSize, futRequest);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long totalListar(FutListadoRequest futRequest) throws Exception {
		if(StringUtils.isBlank(futRequest.getCodigoOficina())) return 0L;
		return futRepository.totalListar(futRequest);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void insertar(Fut fut) throws Exception {
		fut.setCanoAnosol(String.valueOf(LocalDateTime.now().getYear()));
	}
	
	
	@Override
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		return futRepository.listarOficinas(anio, codigoUsuario);
	}
	
	@Override
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception {
		return futRepository.listarEmpleadosPorOficina(codigoOficina);
	}
	
	@Override
	public List<Parametro> listarEstados() throws Exception {
		return futRepository.listarEstados();
	}
	
	@Override
	public List<Parametro> listarMotivos() throws Exception {
		return futRepository.listarMotivos();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Fut editarFut(String anio, String numeroSolicitud) throws Exception {
		return futRepository.editarFut(anio, numeroSolicitud);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void actualizar(Fut fut) throws Exception {
		futRepository.actualizarSP(fut);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void aprobar(Fut fut) throws Exception {
		futRepository.aprobarSP(fut);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void anular(Fut fut) throws Exception {
		futRepository.anularSP(fut);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<FlujoAprobacion> listarFlujoAprobacion(String anio, String numeroSolicitud) throws Exception {
		return futRepository.listarFlujoAprobacion(anio, numeroSolicitud);
	}
	
//	@Override
//	public List<ObjetoPrueba> listar(String parametro1) throws Exception {
//		return futRepository.listar(parametro1);
//	}
//
//	@Override
//	public void insertar(ObjetoPrueba objeto) throws Exception {
//		futRepository.insertar(objeto);
//	}
//
//	@Override
//	public void actualizar(ObjetoPrueba objeto) throws Exception {
//		futRepository.actualizar(objeto);
//	}

//	@Override
//	public ObjetoPrueba obtener(String id) throws Exception {
//		return futRepository.obtener(id);
//	}

}
