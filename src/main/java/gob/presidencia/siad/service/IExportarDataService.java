package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Reporte;
import gob.presidencia.siad.model.request.ExportarDataListadoRequest;
import gob.presidencia.siad.model.request.GenerarReporteRequest;

public interface IExportarDataService {
		
	public DataTable<Reporte> listarReportes(int pageNo, int pageSize, ExportarDataListadoRequest exportarDataListadoRequest) throws Exception;
	public Reporte obtenerReporte(String id) throws Exception;
	public byte[] generarListadoReporte(GenerarReporteRequest generarReporteRequest, String usuario) throws Exception;
	public List<Parametro> buscarGrupos(String anio, String termino) throws Exception;
	public List<Parametro> buscarArticulos(String anio, String termino) throws Exception;
	public List<Parametro> buscarClases(String anio, String codgrupo, String termino) throws Exception;
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarAnios() throws Exception;
	
}
