package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.Empleado;
import gob.presidencia.siad.model.FlujoAprobacion;
import gob.presidencia.siad.model.Fut;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.FutListadoRequest;

public interface IFUTRepository {
	
	//public List<ObjetoPrueba> listar(String parametro1) throws Exception;
	//public void insertar(ObjetoPrueba objeto) throws Exception;
	//public void actualizar(ObjetoPrueba objeto) throws Exception;
		
	public List<Fut> listar(int pageNo, int pageSize, FutListadoRequest futRequest) throws Exception;
	public Long totalListar(FutListadoRequest futRequest) throws Exception;
	public String insertarSP(Fut fut) throws Exception;
	public Fut editarFut(String anio, String numeroSolicitud) throws Exception;
	public void anularSP(Fut fut) throws Exception;
	public void actualizarSP(Fut fut) throws Exception;
	public void aprobarSP(Fut fut) throws Exception;

	
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Empleado> listarEmpleadosPorOficina(String codigoOficina) throws Exception;
	public List<Parametro> listarEstados() throws Exception;
	public List<Parametro> listarMotivos() throws Exception;
	
	public List<FlujoAprobacion> listarFlujoAprobacion(String anio, String numeroSolicitud) throws Exception;
	
	
	//public ObjetoPrueba obtener(String id) throws Exception;
}
