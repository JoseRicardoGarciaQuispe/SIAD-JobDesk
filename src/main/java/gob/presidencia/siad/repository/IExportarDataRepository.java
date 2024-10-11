package gob.presidencia.siad.repository;

import java.util.List;
import java.util.Map;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Reporte;
import gob.presidencia.siad.model.request.ExportarDataListadoRequest;
import gob.presidencia.siad.model.request.GenerarReporteRequest;

public interface IExportarDataRepository {
	
	public DataTable<Reporte> listarSP(int pageNo, int pageSize, ExportarDataListadoRequest exportarDataListadoRequest) throws Exception;
	public Reporte obtenerReporteSP(String id) throws Exception;
	public List<Map<String, Object>> generarListadoReporteSP(GenerarReporteRequest generarReporteRequest) throws Exception;
	public List<Parametro> buscarGruposSP(String anio, String termino) throws Exception;
	public List<Parametro> buscarArticulosSP(String anio, String termino) throws Exception;
	public List<Parametro> buscarClasesSP(String anio, String codgrupo, String termino) throws Exception;
	public List<Parametro> listarOficinasSP(String anio, String codigoUsuario) throws Exception;
	public List<Parametro> listarAniosSP() throws Exception;
	
}