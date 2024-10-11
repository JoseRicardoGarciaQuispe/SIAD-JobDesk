var tablaFuts;
var tablaEmpleados;

function configuraciones() {
	
	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	})

	inicializarTabla();
	
	$('#selectMotivo').val('');
	$('#selectEstado').val('');
	var fechaFin = new Date().toISOString().slice(0, 10);
	var fechaInicio = new Date();
	fechaInicio.setDate(1);

	$('#fechaFutInicio').val('2024-01-01');
//	$('#fechaFutInicio').val(fechaInicio.toISOString().slice(0, 10));
	$('#fechaFutFin').val(fechaFin);
//	$('#fechaLicInicio').val(fechaInicio.toISOString().slice(0, 10));
//	$('#fechaLicFin').val(fechaFin);
	
	
	var selectOficina = document.getElementById("selectOficina");

    if (selectOficina.options.length > 0) {
        buscarFuts();
    }else{
		toastr.info('No se encontraron oficinas relacionadas al módulo','INFO');
	}
}

function inicializarTabla(){
	tablaFuts = $('#tableFuts').DataTable({
		columnDefs:[{
            targets: "_all",
            sortable: false
        }],
        ordering: false,
        "deferLoading": 0,
		processing: false,
		serverSide: true,
		"info": true,
		ajax: {
			url: '/siad/rest/asistencia/fut/buscar',
			method: 'POST',
			contentType: 'application/json',
			data: function(d) {

				var codigoOficina = $("#selectOficina option:selected").val();
				console.log("Cod. Oficina -> ",codigoOficina )
				var codigoEmpleado = $('#txtCodEmpleado').val();
				console.log("Cod. Empleado -> ",codigoEmpleado )
				var codigoMotivo = $("#selectMotivo option:selected").val();
				console.log("Cod. Motivo -> ",codigoMotivo )
				var codigoEstado = $("#selectEstado option:selected").val();
				console.log("Cod. Estado -> ",codigoEstado )

				var fechaFutInicio = $('#fechaFutInicio').val();
				var fechaFutFin = $('#fechaFutFin').val();
				var fechaLicInicio = $('#fechaLicInicio').val();
				var fechaLicFin = $('#fechaLicFin').val();
				
				
				var data = {
					codigoOficina: codigoOficina,
					codigoEmpleado: codigoEmpleado,
					codigoMotivo: codigoMotivo,
					codigoEstado: codigoEstado,
					fechaFutInicio: fechaFutInicio,
					fechaFutFin: fechaFutFin,
					fechaLicInicio: fechaLicInicio,
					fechaLicFin: fechaLicFin,
					start: d.start,
					draw: d.draw,
					length: d.length
				}
				
				console.log("data -> ", JSON.stringify(data));

				return JSON.stringify(data);
			},
			dataSrc: function(response) {
				return response.data;
			},
			beforeSend: function() {
				$('#modalLoading').modal('show');
			},
			complete: function() {
				$('#modalLoading').modal('hide');
			},
			error: function(xhr, status, error) {
				mostrarAlertaError(xhr, status, error);
			}
		},
		"columns": [
			{ "width": "5%", "data": "numeroFut" }, 
			{ "width": "20%", "data": "nomEmpleado" }, 
			{ "width": "5%", "data": "nombreOficina" }, 
			{ "width": "10%", "data": "fechaInicioFut" }, 
			{ "width": "10%", "data": "fechaFinFut" }, 
			{ "width": "5%", "data": "numeroDias" }, 
			{ "width": "10%", "data": "fechaSolicitudFut" }, 
			{ "width": "15%", "data": "motivo" }, 
			{ "width": "15%", "data": "estado" }, 
			{ "width": "5%" }//OPCIONES
		],
		columnDefs: [
			{
				targets: [0, 2, 3, 4, 5, 6, 7, 8],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
			
			{
				targets: 1,
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
			
			{
				targets: 9,
				render: function(data, type, row, meta) {
					
					var columna = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;">';
						columna += '<button ' +
							'type="button" class="btn btn-link" style="padding: 4px 4px!important" title="Ver historial" onclick="verHistorial(\'' + row.canoAnosol + '-' + row.numeroFut + '\')">' +
							'<i class="fa-solid fa-eye fa-lg" style="color: #a91414;"></i>' +
							'</button>' +
							'<a href="/siad/asistencia/fut/editar/' + row.canoAnosol + '-' + row.numeroFut + '" ' +
							'class="btn btn-link" title="Editar" style="padding: 4px 4px!important">' +
							'<i class="fa-solid fa-pen-to-square fa-lg" style="color: #a91414;"></i>' +
							'</a>';
					console.log("Valores: "+row.canoAnosol+" & " +row.numeroFut);
						columna += '</div>';
					return columna;
				}
			}
		],
		scrollX: true,
		pageLength: 10,
		"paging": true,
		searching: false,
		"lengthChange": false,
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

function verHistorial(id) {
	$.ajax({
		url: '/siad/asistencia/fut/aprobaciones/' + id,
		type: 'GET',
		processData: false,
		contentType: false,
		success: function(response) {
			$("#modalVerHistorial").html(response);
			$("#modalVerHistorial").modal("show");
		},
		error: function(xhr, status, error) {
			mostrarAlertaError(xhr, status, error);
		}
	});
}



function buscarFuts() {
	if(validarFormBuscarFuts()){
		tablaFuts.ajax.reload();
	}
}

function validarFormBuscarFuts() {
	if (document.getElementById("selectOficina").options.length == 0) {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
		return false;
	}

	if ($("#selectOficina option:selected").val() == '') {
		toastr.info("Seleccionar la oficina", 'INFO');
		return false;
	}
	return true;
}

/**********************  FUNCIONES PANTALLA REGISTRAR *********************/
function registrarFut(){
	if(validarFormRegistroFut()){
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Registrando . . .');
		var fechaSolicitud = $('#fechaSolicitud').val();
		var codigoOficinaSolicitud = $("#selectOficina option:selected").val();
		var codigoEmpleado = $("#txtCodEmpleado").val();
		var motivo = $("#motivoSolicitud option:selected").val();
		var fechaInicioFut = $('#fechaInicioFut').val();
		var fechaFinFut = $('#fechaFinFut').val();
		var horaInicio = $('#horaInicio').val();
		var horaFin = $('#horaFin').val();
		var just = $('#justSolicitud').val();
		
		var data = {
			
			ffutFecsol: fechaSolicitud,
			cofiCodofi: codigoOficinaSolicitud,
			cempCodemp: codigoEmpleado,
			cmotCodmot: motivo,
			ffutHorini: horaInicio,
			ffutHorfin: horaFin,
			flicFecini: fechaInicioFut,
			flicFecfin: fechaFinFut,
			just: just
	
		};
		
		console.log("data -> ", JSON.stringify(data));
	
		$.ajax({
			url: '/siad/rest/asistencia/fut/registrar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#message').val("Registro exitoso");
				document.getElementById("formMain").submit();
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				if (xhr.responseText) {
					var responseData = JSON.parse(xhr.responseText);
					var errors = '';
					$.each(responseData, function(index, value) {
						errors += '<li style="padding-left: 1em;"><i class="fas fa-exclamation-circle mr-2"></i> ' + value + '</li>';
					});
					toastr.error('<ul>' + errors + '</ul>', 'Error');
				} else {
					toastr.error('Error del sistema', 'Error');
				}
			}
		});
	}
}


function verBuscarEmpleados() {
    var codigoOficina = $("#selectOficina option:selected").val();
    if (!codigoOficina || codigoOficina == '') {
        toastr.info('Seleccionar la oficina', 'INFO');
        return;
    } else {
        $('#modalLoading').modal('show');
        $.ajax({
            url: '/siad/asistencia/fut/empleados/' + codigoOficina,
            type: 'GET',
            processData: false,
            contentType: false,
            success: function(response) {
                $('#modalLoading').modal('hide');
                $("#modalBuscarEmpleados").html(response);
                $('#modalBuscarEmpleados').modal('show');
				
				var table = $('#tableEmpleados').DataTable({
					"columns": [
						{ "width": "40%" }, //NUMERO DE ITEM RACION
						{ "width": "60%" } //CODIGO DE TIPO DE RACION
					],
					scrollY: true,
					columnDefs: [{
						targets: "_all",
						sortable: false
					}],
					pageLength: 15,
					ordering: false,
					"paging": true,
					searching: true,
					"lengthChange": false,
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
						emptyTable: "No se encontraron registros"
					}
			
				});
				table.columns.adjust().draw();
            },
            error: function(xhr, status, error) {
                $('#modalLoading').modal('hide');
                mostrarAlertaError(xhr, status, error);
            }
        });
    }
} 


/**********************  FUNCIONES PANTALLA EDITAR *********************/

function initModalesEditar() {
	
	var anioSolicitud = $('#anioSolicitud').val();
	var numeroSolicitud = $('#txtNumeroSolicitud').val();
	var fechafutSolicitud = $('#fechafutSolicitud').val();
	
	var data = {
		canoAnosol: anioSolicitud,
		csolNumsol: numeroSolicitud,
		fechafutSolicitud: fechafutSolicitud
	};
	
	console.log("data -> ", JSON.stringify(data));
			
	$('#confirmDeleteModal').find('.delete-confirm-button').click(function() {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Cargando . . .');
		$.ajax({
			url: '/siad/rest/asistencia/fut/anular',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#message').val("Registro anulado correctamente");
				document.getElementById("formMain").submit();
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
		$('#confirmDeleteModal').modal('hide'); 
	});
	
	$('#confirmApproveModal').find('.approve-confirm-button').click(function() {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Cargando . . .');
		$.ajax({
			url: '/siad/rest/asistencia/fut/aprobar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				toastr.success('Registro aprobado correctamente', 'OK');
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
		$('#confirmApproveModal').modal('hide'); 
	});
}


function validarFormRegistroFut(){
	if($("#fechaSolicitud").val() == ''){
		toastr.info("Seleccionar fecha de solicitud.", 'Info');
		return false;
	}
	if($("#oficinaSolicitud option:selected").val() == ''){
		toastr.info("Seleccionar la oficina.", 'Info');
		return false;
	}
	if($("#txtCodEmpleado").val() == ''){
		toastr.info("El código del empleado debe ser diferente a nulo.", 'Info');
		return false;
	}
	if($("#txtEmpleado").val() == ''){
		toastr.info("Seleccione un empleado.", 'Info');
		return false;
	}
	if($("#txtCargo").val() == ''){
		toastr.info("El cargo del empleado debe ser diferente a vacío.", 'Info');
		return false;
	}
	if($("#motivoSolicitud option:selected").val() == ''){
		toastr.info("Seleccionar el motivo.", 'Info');
		return false;
	}
	if($("#fechaInicioFut").val() == ''){
		toastr.info("Seleccionar fecha de inicio", 'Info');
		return false;
	}
	if($("#fechaFinFut").val() == ''){
		toastr.info("Seleccionar fecha de fin", 'Info');
		return false;
	}
	if($("#horaInicio").val() == ''){
		toastr.info("Seleccione la hora de inicio.", 'Info');
		return false;
	}
	if($("#horaFin").val() == ''){
		toastr.info("Seleccione la hora de término.", 'Info');
		return false;
	}
	if($("#justSolicitud").val() == ''){
		toastr.info("Indique la justificación.", 'Info');
		return false;
	}
	return true;
}


function editarFut(){
	if(validarFormRegistroFut()){
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Guardando . . .');
		var anioSolicitud = $('#anioSolicitud').val();
		var numeroSolicitud = $('#numeroSolicitud').val();
		var fechaSolicitud = $('#fechaSolicitud').val();
		var codigoOficinaSolicitud = $("#selectOficina option:selected").val();
		var codigoEmpleado = $("#txtCodEmpleado").val();
		var motivo = $("#motivoSolicitud option:selected").val();
		var fechaInicioFut = $('#fechaInicioFut').val();
		var fechaFinFut = $('#fechaFinFut').val();
		var horaInicio = $('#horaInicio').val();
		var horaFin = $('#horaFin').val();
		var just = $('#justSolicitud').val();
		
		
		var data = {
			canoAnosol: anioSolicitud,
			csolNumsol: numeroSolicitud,
			ffutFecsol: fechaSolicitud,
			cofiCodofi: codigoOficinaSolicitud,
			cempCodemp: codigoEmpleado,
			cmotCodmot: motivo,
			ffutHorini: horaInicio,
			ffutHorfin: horaFin,
			flicFecini: fechaInicioFut,
			flicFecfin: fechaFinFut,
			just: just
		};
	
		$.ajax({
			url: '/siad/rest/asistencia/fut/editar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				toastr.success('Registro modificado correctamente', 'OK');
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
	}
	
}


/*	Función para elegir el empleado de la tabla 	*/

$(document).ready(function() {
	
    $('#tableEmpleados tbody').on('click', 'tr', function () {
        
        var codigoEmpleado = $(this).find('td:nth-child(1)').text().trim();
        var nombreEmpleado = $(this).find('td:nth-child(2)').text().trim();
        var cargoEmpleado = $(this).find('td:nth-child(3)').text().trim();
        
        $('#txtCodEmpleado').val(codigoEmpleado);
        $('#txtEmpleado').val(nombreEmpleado);

        if ($('.cargoField').length) {
            	$('#txtCargo').val(cargoEmpleado);
            	console.log("Cargo: " + cargoEmpleado);
        }
        $('#modalBuscarEmpleados').modal('hide');
    });
});



