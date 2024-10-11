// horasCompensables.js

// Variable global para la tabla de saldo vacacional
var tablaHorasCompensables;

/**
 * Función que se ejecuta al cargar la página. Configura la tabla y otras configuraciones necesarias.
 */
function configuraciones() {
    // Configura el comportamiento del modal de carga
    $('#modalLoading').on('hidden.bs.modal', function(event) {
        $('#messageModalLoading').text('Cargando . . .');
    });

    // Inicializa la tabla de HORAS COMPENSABLES
    inicializarTabla();

    // Limpia los campos de selección de año y mes
    $('#selectAnio').val('');
    $('#selectMes').val('');
    var selectOficina = document.getElementById("selectOficina");
    var selectAnio = document.getElementById("selectAnio");
}

/**
 * Inicializa la tabla de HORAS COMPENSABLES usando DataTables.
 * Configura las columnas, la paginación y el comportamiento de los datos.
 */
function inicializarTabla() {
    tablaHorasCompensables = $('#tableHorasCompensables').DataTable({
        // Configuración para deshabilitar la ordenación en todas las columnas
        columnDefs: [{
            targets: "_all",
            sortable: false
        }],
        ordering: false,
        "deferLoading": 0,  // No cargar datos hasta que se solicite
        processing: false,
        serverSide: true,  // Indica que los datos vendrán del servidor
        "info": true,
        ajax: {
            // URL del servidor para obtener los datos
            url: '/siad/rest/asistencia/horas-compensables/buscar',
            method: 'POST',
            contentType: 'application/json',
            data: function(d) {
                // Recoge los datos del formulario para enviar al servidor
                var anio = $("#selectAnio option:selected").val();
                var mes = $('#selectMes').val();
                var codigoOficina = $("#selectOficina option:selected").val();

                var data = {
                    codigoOficina: codigoOficina,
                    anio: anio,
                    mes: mes,                   
                    start: d.start,
                    draw: d.draw,
                    length: d.length
                };
                console.log(JSON.stringify(data));
                // Convierte los datos a formato JSON
                return JSON.stringify(data);
            },
            dataSrc: function(response) {
                // Procesa la respuesta y devuelve los datos a la tabla
                console.log(response);
                return response.data;
            },
            beforeSend: function() {
                // Muestra el modal de carga antes de realizar la petición
                $('#modalLoading').modal('show');
            },
            complete: function() {
                // Oculta el modal de carga una vez que la petición ha finalizado
                $('#modalLoading').modal('hide');
            },
            error: function(xhr, status, error) {
                // Muestra un mensaje de error si la petición falla
                mostrarAlertaError(xhr, status, error);
            }
        },
       // Configuración de las columnas de la tabla
"columns": [
    { "width": "15%", "data": "CODIGO_EMPLEADO" }, 
    { "width": "20%", "data": "NOMBRE_EMPLEADO" }, 
    { "width": "10%", "data": "FECHA_INGRESO" }, 
    { "width": "10%", "data": "OFICINA" }, 
    { "width": "10%", "data": "EXTRAB" }, 
    { "width": "10%", "data": "EJECB" }, 
    { "width": "10%", "data": "DISPONIBLEB" }  // Columna "Saldo"
],
// Configura el renderizado de las celdas para alinear el contenido
columnDefs: [
    {
        targets: [0, 1, 2, 3, 5, 6],  // Añade el índice 6 que corresponde a la columna "Saldo"
        render: function(data, type, row, meta) {
            return '<div style="text-align: center; font-size: 13px; padding: 8px 0;">' + data + '</div>';
        }
    },
    {
        targets: [4],
        render: function(data, type, row, meta) {
            return '<div style="text-align: center; font-size: 16px; padding: 8px 0;">' + 
                   '<span class="badge bg-light text-dark">' + data + '</span></div>';
        }
    }
]
,
        scrollX: true,  // Habilita el desplazamiento horizontal si es necesario
        pageLength: 10,  // Número de registros por página
        "paging": true,  // Habilita la paginación
        searching: false,  // Deshabilita la búsqueda global
        "lengthChange": false,  // Oculta la opción de cambiar el número de registros por página
        "language": {
            "paginate": {
                "first": "Primera",
                "last": "Última",
                "next": "Siguiente",
                "previous": "Anterior"
            },
            "info": "Total: _TOTAL_ registros",
            "infoEmpty": "Total: 0 registros",
            "infoFiltered": "(filtrado de _MAX_ registros totales)",
            "search": "Buscar:",
            emptyTable: "No se encontraron elementos"
        }
    });
}

/**
 * Función para realizar la búsqueda de HORAS COMPENSABLES.
 * Recarga los datos de la tabla basándose en los parámetros ingresados.
 */
function buscarHorasCompensables() {
    // Valida que el formulario de búsqueda sea válido antes de continuar
    if (validarFormBuscarHorasCompensables()) {
        // Recarga la tabla con los nuevos datos
        tablaHorasCompensables.ajax.reload();
    }
}

/**
 * Función para validar el formulario de búsqueda.
 * Verifica que se haya seleccionado un año y un mes.
 * @returns {boolean} true si el formulario es válido, false de lo contrario.
 */
function validarFormBuscarHorasCompensables() {
    var anio = $('#selectAnio').val();
    var mes = $('#selectMes').val();

    if (anio === '') {
        toastr.info("Seleccionar el año", 'INFO');
        return false;
    }
    if (mes === '') {
        toastr.info("Seleccionar el mes", 'INFO');
        return false;
    }

    return true;
}
