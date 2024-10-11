package gob.presidencia.siad.service;

import java.util.List;

import gob.presidencia.siad.model.MenuSemanal;
import gob.presidencia.siad.model.Parametro;
import gob.presidencia.siad.model.Semana;

public interface IMenuSemanalService {

	public List<Parametro> listarAnios() throws Exception;
	public List<Parametro> listarDias(String anio, String numSemana) throws Exception;
	public List<Semana> listarSemanas(String anio) throws Exception;
	public List<MenuSemanal> listarMenuSemanal(String anio, String numSemana, String fecha) throws Exception;

}
