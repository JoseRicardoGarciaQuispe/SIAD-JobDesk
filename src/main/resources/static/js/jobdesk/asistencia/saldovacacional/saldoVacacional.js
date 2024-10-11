// saldoVacacional.js

// Variable global para la tabla de saldo vacacional
var tablaSaldoVacacional;

/**
 * Función que se ejecuta al cargar la página. Configura la tabla y otras configuraciones necesarias.
 */
function configuraciones() {
    // Configura el comportamiento del modal de carga
    $('#modalLoading').on('hidden.bs.modal', function(event) {
        $('#messageModalLoading').text('Cargando . . .');
    });

    // Inicializa la tabla de saldo vacacional
    inicializarTabla();

    // Limpia el campo de empleado
    $('#txtEmpleado').val('');

    // Verifica si hay oficinas en el selector para realizar la búsqueda
    var selectOficina = document.getElementById("selectOficina");

    if (selectOficina.options.length > 0) {
        // Si hay oficinas disponibles, se realiza la búsqueda de saldo vacacional
        buscarSaldoVacacional();
    } else {
        // Si no hay oficinas disponibles, muestra un mensaje informativo
        toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
    }
}

/**
 * Inicializa la tabla de saldo vacacional usando DataTables.
 * Configura las columnas, la paginación y el comportamiento de los datos.
 */
function inicializarTabla() {
    tablaSaldoVacacional = $('#tableSaldoVacacional').DataTable({
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
            url: '/siad/rest/asistencia/saldo-vacacional/buscar',
            method: 'POST',
            contentType: 'application/json',
            data: function(d) {
                // Recoge los datos del formulario para enviar al servidor
                var codigoOficina = $("#selectOficina option:selected").val();
                var empleado = $('#txtEmpleado').val();

                var data = {
                    codigoOficina: codigoOficina,
                    empleado: empleado,
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
            { "width": "15%", "data": "CODEMPLEADO" },
            { "width": "20%", "data": "APENOMEMPLEADO" },
            { "width": "10%", "data": "FECHAINGRESO" },
            { "width": "10%", "data": "NOMOFICINA" },
            { "width": "10%", "data": "PENDANIOSANT" },
            { "width": "10%", "data": "PENDANIOACT" },
            { "width": "15%", "data": "MESPROGRAMADO" },
            { "width": "10%", "data": "TOTALPENDIENTE" }
        ],
        // Configura el renderizado de las celdas para alinear el contenido
        columnDefs: [
            {
                targets: [0, 1, 2, 3, 6],
                render: function(data, type, row, meta) {
                    return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
                }
            },
            {
                targets: [4, 5],
                render: function(data, type, row, meta) {
                    return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 16px;">' + 
                    '<span class="badge bg-light text-dark">' + data + '</span></div>';
                }
            },
            {
                targets: [7],
                render: function(data, type, row, meta) {
                    return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 16px;">' + 
                    '<span class="badge bg-primary text-white">' + data + '</span></div>';
                }
            }
        ],
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
 * Función para realizar la búsqueda de saldo vacacional.
 * Recarga los datos de la tabla basándose en los parámetros ingresados.
 */
function buscarSaldoVacacional() {
    // Valida que el formulario de búsqueda sea válido antes de continuar
    if (validarFormBuscarSaldoVacacional()) {
        // Recarga la tabla con los nuevos datos
        tablaSaldoVacacional.ajax.reload();//Ejecuta la parte AJAX donde se incializa la tabla
    }
}

/**
 * Función para validar el formulario de búsqueda.
 * Verifica que se haya seleccionado una oficina.
 * @returns {boolean} true si el formulario es válido, false de lo contrario.
 */
function validarFormBuscarSaldoVacacional() {
    // Verifica si hay oficinas disponibles en el selector
    if (document.getElementById("selectOficina").options.length == 0) {
        toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
        return false;
    }

    // Verifica si se ha seleccionado una oficina
    if ($("#selectOficina option:selected").val() == '') {
        toastr.info("Seleccionar la oficina", 'INFO');
        return false;
    }
    return true; // Añadir esto para completar la función correctamente
}