package gob.presidencia.siad.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gob.presidencia.siad.model.DataTable;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Reporte;
import gob.presidencia.siad.model.request.ExportarDataListadoRequest;
import gob.presidencia.siad.model.request.GenerarReporteRequest;
import gob.presidencia.siad.repository.IExportarDataRepository;
import gob.presidencia.siad.service.IExportarDataService;
import gob.presidencia.siad.util.ExportarUtil;

@Service
public class ExportarDataServiceImpl implements IExportarDataService {
	
	@Autowired
	private IExportarDataRepository exportarDataRepository;
	
	@Autowired
	private ExportarUtil exportarUtil;
	
	@Override
	@Transactional(readOnly = true)
	public DataTable<Reporte> listarReportes(int pageNo, int pageSize, ExportarDataListadoRequest exportarDataListadoRequest) throws Exception {
		return exportarDataRepository.listarSP(pageNo, pageSize, exportarDataListadoRequest);
	}

	@Override
	@Transactional(readOnly = true)
	public Reporte obtenerReporte(String id) throws Exception {
		return exportarDataRepository.obtenerReporteSP(id);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] generarListadoReporte(GenerarReporteRequest generarReporteRequest, String usuario) throws Exception {
		List<Map<String, Object>> list = exportarDataRepository.generarListadoReporteSP(generarReporteRequest);
		Reporte reporte = this.obtenerReporte(generarReporteRequest.getId());
		reporte.setUsuario(usuario);
		return exportarUtil.exportarExcel(list, reporte);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> buscarGrupos(String anio, String termino) throws Exception {
		return exportarDataRepository.buscarGruposSP(anio, termino);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> buscarArticulos(String anio, String termino) throws Exception {
		return exportarDataRepository.buscarArticulosSP(anio, termino);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> buscarClases(String anio, String codgrupo, String termino) throws Exception {
		return exportarDataRepository.buscarClasesSP(anio, codgrupo, termino);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarOficinas(String anio, String codigoUsuario) throws Exception {
		return exportarDataRepository.listarOficinasSP(anio, codigoUsuario);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Parametro> listarAnios() throws Exception {
		return exportarDataRepository.listarAniosSP();
	}

}
