package gob.presidencia.siad.model.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase personalizada que extiende la clase User de Spring Security.
 * Esta clase añade más detalles del usuario autenticado, como el código de empleado, nombres, año, etc.
 * Además de la información estándar proporcionada por Spring Security (nombre de usuario, contraseña, y roles/autorizaciones).
 */
@Getter
@Setter
public class UserDetails extends User {

    private static final long serialVersionUID = 1L;

    // Atributos adicionales que se usarán junto con los datos del usuario autenticado
    private String codigoEmpleado;  // Código del empleado
    private String nombres;         // Nombres completos del empleado
    private String anio;            // Año relacionado con la operación
    private String codinstitucion;  // Código de la institución del empleado
    private String periodo;         // Periodo (puede ser un año académico, fiscal, etc.)
    private String urlLogin;        // URL de inicio de sesión (si es necesario redirigir al usuario)

    /**
     * Constructor que recibe el nombre de usuario, la contraseña y las autoridades.
     * Este constructor es utilizado por Spring Security para crear el objeto del usuario autenticado.
     *
     * @param username El nombre de usuario.
     * @param password La contraseña del usuario.
     * @param authorities La colección de roles/autorizaciones del usuario.
     */
    public UserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        // Llama al constructor de la clase padre User (de Spring Security)
        super(username, password, authorities);
    }
}
