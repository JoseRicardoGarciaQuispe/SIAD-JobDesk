package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoAprobacion;
import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.FutListadoRequest;

public interface IFutService {

	public List<Fut> listar (int pageNo, int pageSize, FutListadoRequest futListadoRequest) throws Exception;
	public Long totalListar(FutListadoRequest futListadoRequest) throws Exception;
	public void insertar(Fut fut) throws Exception;
	public Fut editarFut(String anio, String numeroSolicitud) throws Exception;
	public void anular(Fut fut) throws Exception;
	public void actualizar(Fut fut) throws Exception;
	public void aprobar(Fut fut) throws Exception;
	
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception;
	public List<Parametro> listarEstados() throws Exception;
	public List<Parametro> listarMotivos() throws Exception;
	
	public List<FlujoAprobacion> listarFlujoAprobacion(String anio, String numeroSolicitud) throws Exception;

	
}
