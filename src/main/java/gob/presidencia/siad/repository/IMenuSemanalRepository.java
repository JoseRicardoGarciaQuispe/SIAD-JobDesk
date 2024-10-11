package gob.presidencia.siad.repository;

import java.util.List;

import gob.presidencia.siad.model.Semana;
import gob.presidencia.siad.model.MenuSemanal;
import gob.presidencia.siad.model.Parametro;

public interface IMenuSemanalRepository {
	

	public List<Parametro> listarAniosSP() throws Exception;
	public List<Parametro> listarDiasSP(String anio, String numSemana) throws Exception;
	public List<Semana> listarSemanaSP(String anio) throws Exception;
	public List<MenuSemanal> listarMenuSemanalSP(String anio, String numSemana, String fecha) throws Exception;
}
