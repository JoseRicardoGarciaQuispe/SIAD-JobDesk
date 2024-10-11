package gob.presidencia.siad.model.request;

import lombok.Data;

@Data
public class HorasCompensablesListadoRequest {
    private String codigoEmpleado;  // Código del empleado, obtenido del UserDetails
    private String anio;            // Año para la consulta de horas compensables
    private String mes;             // Mes para la consulta de horas compensables
    private String codigoUsuario;   // Código del usuario autenticado (opcional)
    private String codigoOficina;
    /*
     * Campos para paginacion del dataTable
     */
    private int draw;
    private int start;
    private int length;
}


    //private String codigoOficina;
	//private String empleado;
	//private String codigoEmpleado;
	//private String anio;
	//private String codigoUsuario;