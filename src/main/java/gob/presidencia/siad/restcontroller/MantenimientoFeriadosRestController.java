package gob.presidencia.siad.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/asistencia/mantenimiento-feriados")
public class MantenimientoFeriadosRestController {

    private static final Logger logger = LoggerFactory.getLogger(MantenimientoFeriadosRestController.class);

    //@Autowired
    //private IMantenimientoFeriadosService;

    //@Autowired
    //private MantenimientoFeriadosValidator;

    @PostMapping(value = "/buscar")
    public ResponseEntity<?> buscarFeriado(@RequestBody MantenimientoFeriadosListadoRequest MantenimientoFeriadosRequest){
        try{
            List<String> errors = 
        }
    }

}
