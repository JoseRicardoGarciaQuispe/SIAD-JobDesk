package gob.presidencia.siad.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

import gob.presidencia.siad.exception.ValidacionPersonalizadaException;
import gob.presidencia.siad.model.Reporte;

@Component
public class RepositoryUtil {
	
	private static SimpleDateFormat simpleDateFormatOUT = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat simpleDateFormatIN = new SimpleDateFormat("yyyy-MM-dd");

	public void analizarResultado(Map<String, Object> result) throws ValidacionPersonalizadaException, Exception {
		Integer flagResultado = null;
		flagResultado = (Integer) result.get("p_nFLAG_RESULTADO");
	    String mensajeResultado = (String) result.get("p_vMENSAJE_RESULTADO");
	
	    /*
	     * -1: Excepcion
	     *  1: OK
	     *  2: Mensaje de validacion personalizado
	     */
	    switch(flagResultado) {
		    case -1: throw new Exception(mensajeResultado);
		    case 2: throw new ValidacionPersonalizadaException(mensajeResultado);
		    case 1: break;
		    default: throw new Exception("Flag no encontrado");
	    }
	}
	
	public Reporte castToReporte(Map<String, Object> map) {
		Reporte reporte = new Reporte();
    	reporte.setId(map.get("ID")!=null?map.get("ID").toString():null);
    	reporte.setDescripcion(map.get("DESCRIPCION")!=null?map.get("DESCRIPCION").toString():null);
    	reporte.setComentario(map.get("COMENTARIO")!=null?map.get("COMENTARIO").toString():null);
    	reporte.setUsuarioReg(map.get("USUARIO_REG")!=null?map.get("USUARIO_REG").toString():null);
    	return reporte;
	}
	
	public String castDateFormat(String dateString) throws ParseException {
		if(StringUtils.isBlank(dateString)) return null;
		Date date = simpleDateFormatIN.parse(dateString);
		String output = simpleDateFormatOUT.format(date);
		return output;
	}
	
	public <T> List<T> castToList(Class<T> clase, List<Map<String, Object>> list) {
        List<T> lista = new ArrayList<>();
        for(Map<String, Object> item : list) {
			ObjectMapper mapper = JsonMapper.builder()
					.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
					.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
					.build()
					;
			lista.add(mapper.convertValue(item, clase));
		}
        return lista;
    }
}
