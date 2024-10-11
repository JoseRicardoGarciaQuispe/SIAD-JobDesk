package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Tardanzas;

import gob.presidencia.siad.model.request.TardanzasListadoRequest;

public interface ITardanzasService {

	public List<Tardanzas> listar(int pageNo, int pageSize, TardanzasListadoRequest tardanzasRequest) throws Exception;
	public Long totalListar(TardanzasListadoRequest tardanzasRequest) throws Exception;
	
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarEmpleadosPorOficina(String anio, String codigoUsuario, String codigoOficina) throws Exception;
	
}
