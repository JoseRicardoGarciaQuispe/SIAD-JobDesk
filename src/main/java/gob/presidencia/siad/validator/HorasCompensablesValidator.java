package gob.presidencia.siad.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gob.presidencia.siad.model.request.HorasCompensablesListadoRequest;

/**
 * Validador personalizado para las solicitudes relacionadas con la búsqueda de Horas Compensables.
 */
@Component
public class HorasCompensablesValidator {

    /**
     * Método que valida los datos proporcionados en la solicitud de búsqueda de horas compensables.
     * Verifica si los campos obligatorios están presentes y si tienen valores correctos.
     *
     * @param horasCompensablesListadoRequest Objeto que contiene los datos de la solicitud.
     * @return Una lista de errores, si los hay. Si no hay errores, la lista estará vacía.
     * @throws Exception En caso de algún error al realizar la validación.
     */
    public List<String> validateSearch(HorasCompensablesListadoRequest horasCompensablesListadoRequest) throws Exception {
        List<String> errors = new ArrayList<>();
        try {
             // Validar que el año no esté vacío
             if (StringUtils.isBlank(horasCompensablesListadoRequest.getAnio())) {
                errors.add("El año es obligatorio.");
            }

            // Validar que el mes no esté vacío
            if (StringUtils.isBlank(horasCompensablesListadoRequest.getMes())) {
                errors.add("El mes es obligatorio.");
            }

            return errors;
        } catch (Exception e) {
            // Si ocurre alguna excepción durante la validación, se lanza
            throw e;
        }
    }
}
