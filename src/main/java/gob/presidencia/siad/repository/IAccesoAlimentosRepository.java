package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.AccesoAlimentos;
import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.request.AccesoAlimentosListadoRequest;

public interface IAccesoAlimentosRepository {
	
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception;
	public DataTable<AccesoAlimentos> listarSP(int pageNo, int pageSize, AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) throws Exception;
	public List<AccesoAlimentos> listarAllSP(AccesoAlimentosListadoRequest accesoAlimentosListadoRequest) throws Exception;
	
}