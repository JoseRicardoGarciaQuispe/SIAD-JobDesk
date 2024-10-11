package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoEstado;
import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.NotaAlimentoListadoRequest;

public interface INotasAlimentosService {

	public List<NotaAlimento> listar(int pageNo, int pageSize, NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception;
	public Long totalListar(NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception;
	public void insertar(NotaAlimento notaAlimento) throws Exception;
	public void actualizar(NotaAlimento notaAlimento) throws Exception;
	public void anular(NotaAlimento notaAlimento) throws Exception;
	public void aprobar(NotaAlimento notaAlimento) throws Exception;
	public String esAprobador(NotaAlimento notaAlimento) throws Exception;
	public NotaAlimento obtenerNotaAlimento(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception;
	public List<FlujoEstado> listarFlujoEstados(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception;
		
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarTipoDocumentos() throws Exception;
	public List<Parametro> listarTipoNotas() throws Exception;
	public List<Parametro> listarEstados() throws Exception;
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception;
	public List<Empleado> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina, String indicadorApoyo) throws Exception;
	public List<Parametro> listarTipoRaciones() throws Exception;
	public List<Parametro> listarTipoMotivos() throws Exception;
}
