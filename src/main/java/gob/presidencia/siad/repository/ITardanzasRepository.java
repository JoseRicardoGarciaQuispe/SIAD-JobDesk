
package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Tardanzas;
import gob.presidencia.siad.model.request.TardanzasListadoRequest;

public interface ITardanzasRepository {
	
	public List<Tardanzas> listar(int pageNo, int pageSize, TardanzasListadoRequest tardanzasRequest) throws Exception;
	public Long totalListar(TardanzasListadoRequest tardanzasRequest) throws Exception;
	
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarEmpleadosPorOficinaSP(String anio, String codigoUsuario, String codigoOficina) throws Exception;
}
