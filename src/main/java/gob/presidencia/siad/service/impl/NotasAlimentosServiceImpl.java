package gob.presidencia.siad.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoEstado;
import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.NotaAlimentoDetalle;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.NotaAlimentoListadoRequest;
import gob.presidencia.siad.repository.INotasAlimentosRepository;
import gob.presidencia.siad.service.INotasAlimentosService;

@Service
public class NotasAlimentosServiceImpl implements INotasAlimentosService {
	
	@Autowired
	private INotasAlimentosRepository notasAlimentosRepository;

	@Override
	@Transactional(readOnly = true)
	public List<NotaAlimento> listar(int pageNo, int pageSize, NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception {
		if(StringUtils.isBlank(notaAlimentoRequest.getCodigoOficina())) return new ArrayList<>();
		return notasAlimentosRepository.listar(pageNo, pageSize, notaAlimentoRequest);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Long totalListar(NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception {
		if(StringUtils.isBlank(notaAlimentoRequest.getCodigoOficina())) return 0L;
		return notasAlimentosRepository.totalListar(notaAlimentoRequest);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insertar(NotaAlimento notaAlimento) throws Exception {
		String numSol = notasAlimentosRepository.insertarSP(notaAlimento);
		notaAlimento.setCsolNumsol(numSol);
		for(NotaAlimentoDetalle item : notaAlimento.getItems()) {
			item.setCanoAnosol(notaAlimento.getCanoAnosol());
			item.setCdocTipdoc(notaAlimento.getCdocTipdoc());
			item.setCsolNumsol(numSol);
			item.setCofiCodofi(notaAlimento.getCofiCodofi());
			item.setCusuUsureg(notaAlimento.getCusuUsureg());
			notasAlimentosRepository.insertarDetalleSP(item);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void actualizar(NotaAlimento notaAlimento) throws Exception {
		notasAlimentosRepository.actualizarSP(notaAlimento);
		
		if(notaAlimento.getListaEliminar()!=null && !notaAlimento.getListaEliminar().isEmpty()) {
			for(NotaAlimentoDetalle item: notaAlimento.getListaEliminar()) {
				item.setCanoAnosol(notaAlimento.getCanoAnosol());
				item.setCdocTipdoc(notaAlimento.getCdocTipdoc());
				item.setCsolNumsol(notaAlimento.getCsolNumsol());
				item.setCusuUsumod(notaAlimento.getCusuUsumod());
				notasAlimentosRepository.anularDetalleSP(item);
			}
		}
		
		if(notaAlimento.getListaModificar()!=null && !notaAlimento.getListaModificar().isEmpty()) {
			for(NotaAlimentoDetalle item: notaAlimento.getListaModificar()) {
				item.setCanoAnosol(notaAlimento.getCanoAnosol());
				item.setCdocTipdoc(notaAlimento.getCdocTipdoc());
				item.setCsolNumsol(notaAlimento.getCsolNumsol());
				item.setCofiCodofi(notaAlimento.getCofiCodofi());
				item.setCusuUsumod(notaAlimento.getCusuUsumod());
				notasAlimentosRepository.actualizarDetalleSP(item);
			}
		}
		
		if(notaAlimento.getListaAgregar()!=null && !notaAlimento.getListaAgregar().isEmpty()) {
			for(NotaAlimentoDetalle item: notaAlimento.getListaAgregar()) {
				item.setCanoAnosol(notaAlimento.getCanoAnosol());
				item.setCdocTipdoc(notaAlimento.getCdocTipdoc());
				item.setCsolNumsol(notaAlimento.getCsolNumsol());
				item.setCofiCodofi(notaAlimento.getCofiCodofi());
				item.setCusuUsureg(notaAlimento.getCusuUsureg());
				notasAlimentosRepository.insertarDetalleSP(item);
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void aprobar(NotaAlimento notaAlimento) throws Exception {
		notasAlimentosRepository.aprobarSP(notaAlimento);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void anular(NotaAlimento notaAlimento) throws Exception {
		notasAlimentosRepository.anularSP(notaAlimento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		return notasAlimentosRepository.listarOficinas(anio, codigoUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarTipoDocumentos() throws Exception {
		return notasAlimentosRepository.listarTipoDocumentos();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarTipoNotas() throws Exception {
		return notasAlimentosRepository.listarTipoNotas();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarEstados() throws Exception {
		return notasAlimentosRepository.listarEstados();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception {
		return notasAlimentosRepository.listarEmpleadosPorOficina(codigoOficina);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Empleado> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina, String indicadorApoyo) throws Exception {
		return notasAlimentosRepository.listarEmpleadosPorOficinaSP(anio, codigoUsuario, codigoOficina, indicadorApoyo);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarTipoRaciones() throws Exception {
		return notasAlimentosRepository.listarTipoRacionesSP();
	}

	@Override
	@Transactional(readOnly = true)
	public NotaAlimento obtenerNotaAlimento(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception {
		return notasAlimentosRepository.obtenerNotaAlimento(anio, codigoTipoDocumento, numeroSolicitud);
	}

	@Override
	@Transactional(readOnly = true)
	public List<FlujoEstado> listarFlujoEstados(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception {
		return notasAlimentosRepository.listarFlujoEstados(anio, codigoTipoDocumento, numeroSolicitud);
	}

	@Override
	@Transactional(readOnly = true)
	public String esAprobador(NotaAlimento notaAlimento) throws Exception {
		return notasAlimentosRepository.esAprobadorSP(notaAlimento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarTipoMotivos() throws Exception {
		return notasAlimentosRepository.listarTipoMotivosSP();
	}


}
