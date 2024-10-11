package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoEstado;
import gob.presidencia.siad.model.NotaAlimento;
import gob.presidencia.siad.model.NotaAlimentoDetalle;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.NotaAlimentoListadoRequest;

public interface INotasAlimentosRepository {
	
	public List<NotaAlimento> listar(int pageNo, int pageSize, NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception;
	public Long totalListar(NotaAlimentoListadoRequest notaAlimentoRequest) throws Exception;
	public String insertarSP(NotaAlimento notaAlimento) throws Exception;
	public void insertarDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception;
	public void actualizarSP(NotaAlimento notaAlimento) throws Exception;
	public void actualizarDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception;
	public void anularSP(NotaAlimento notaAlimento) throws Exception;
	public void anularDetalleSP(NotaAlimentoDetalle notaAlimentoDetalle) throws Exception;
	public void aprobarSP(NotaAlimento notaAlimento) throws Exception;
	public String esAprobadorSP(NotaAlimento notaAlimento) throws Exception;
	public NotaAlimento obtenerNotaAlimento(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception;
	public List<FlujoEstado> listarFlujoEstados(String anio, String codigoTipoDocumento, String numeroSolicitud) throws Exception;
	
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarTipoDocumentos() throws Exception;
	public List<Parametro> listarTipoNotas() throws Exception;
	public List<Parametro> listarEstados() throws Exception;
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception;
	public List<Empleado> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina, String indicadorApoyo) throws Exception;
	public List<Parametro> listarTipoRacionesSP() throws Exception;
	public List<Parametro> listarTipoMotivosSP() throws Exception;
}
