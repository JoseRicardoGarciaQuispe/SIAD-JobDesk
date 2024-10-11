package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.AccesoAlimentos;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;

public interface IAccesoAlimentosService {
		
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public DataTable<AccesoAlimentos> listarSP(int pageNo, int pageSize, AccesoAlimentosListadoRequest accesoAlimentosRequest) throws Exception;
	public List<AccesoAlimentos> listarAllSP(AccesoAlimentosListadoRequest accesoAlimentosRequest) throws Exception;
}
