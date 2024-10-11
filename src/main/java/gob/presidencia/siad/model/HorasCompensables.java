package gob.presidencia.siad.model;

import lombok.Data;

@Data
public class HorasCompensables {

    private String CODIGO_EMPLEADO;         // Código del empleado
    private String NOMBRE_EMPLEADO;      // Apellidos y nombres del empleado
    private String FECHA_INGRESO;        // Fecha de ingreso del empleado
    private String OFICINA;          // Nombre de la oficina
    private String EXTRAB;         // Sobretiempo acumulado (extra)
    private String EJECB;           // Horas de sobretiempo ejecutadas (ejec)
    private String DISPONIBLEB;               // Horas compensables disponibles (saldo)
}
