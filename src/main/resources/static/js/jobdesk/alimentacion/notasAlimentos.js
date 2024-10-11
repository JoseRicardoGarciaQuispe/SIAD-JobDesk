var tablaNotasAlimentos;
var tablaRaciones;
var valorAntiguoSelectOficina;
var listaAgregar;
var listaModificar;
var listaEliminar;
var selectedRow;

/**********************  FUNCIONES PANTALLA PRINCIPAL *********************/

function configuraciones() {

	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	});

	inicializarTabla();

	$('#selectTipoDocumento').val('310'); //SOLO NOTAS DE ALIMENTACION
	$('#selectTipoNota').val('01'); //SOLO ATENCION

	$("#selectTipoDocumento").prop("disabled", true);
	$("#selectTipoNota").prop("disabled", true);

	$('#selectEstado').val('');
	var fechaFin = new Date().toISOString().slice(0, 10);
	var fechaInicio = new Date();
	fechaInicio.setDate(1);

	//	$('#fechaNotaInicio').val(fechaInicio.toISOString().slice(0, 10));
//	$('#fechaNotaInicio').val('2023-09-01');
	$('#fechaNotaInicio').val(fechaFin);
	$('#fechaNotaFin').val(fechaFin);

	var selectOficina = document.getElementById("selectOficina");

	if (selectOficina.options.length > 0) {
		buscarNotasAlimentos();
	} else {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
	}

}

function inicializarTabla() {
	tablaNotasAlimentos = $('#tableNotasAlimentos').DataTable({
		columnDefs: [{
			targets: "_all",
			sortable: false
		}],
		ordering: false,
		"deferLoading": 0,
		processing: false,
		serverSide: true,
		"info": true,
		ajax: {
			url: '/siad/rest/alimentacion/notas-alimentos/buscar',
			method: 'POST',
			contentType: 'application/json',
			data: function(d) {

				var codigoOficina = $("#selectOficina option:selected").val();
				var codigoTipoDocumento = $("#selectTipoDocumento option:selected").val();
				var codigoTipoNota = $("#selectTipoNota option:selected").val();
				var codigoEstado = $("#selectEstado option:selected").val();

				var fechaNotaInicio = $('#fechaNotaInicio').val();
				var fechaNotaFin = $('#fechaNotaFin').val();

				var data = {
					codigoOficina: codigoOficina,
					codigoTipoDocumento: codigoTipoDocumento,
					codigoTipoNota: codigoTipoNota,
					codigoEstado: codigoEstado,
					fechaNotaInicio: fechaNotaInicio,
					fechaNotaFin: fechaNotaFin,
					start: d.start,
					draw: d.draw,
					length: d.length
				}

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
			{ "width": "10%", "data": "tipoDocumento" },
			{ "width": "5%", "data": "numeroSolicitud" },
			{ "width": "5%", "data": "fechaSolicitud" },
			{ "width": "10%", "data": "tipoSolicitud" },
			{ "width": "40%", "data": "nombreOficina" },
			{ "width": "5%", "data": "fechaInicioSolicitud" },
			{ "width": "5%", "data": "fechaFinSolicitud" },
			{ "width": "10%", "data": "estado" },
			{ "width": "10%" }//OPCIONES
		],
		columnDefs: [
			{
				targets: [0, 1, 2, 3, 4, 5, 6, 7],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
			{
				targets: 8,
				render: function(data, type, row, meta) {
					var columna = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;">';
					columna += '<button ' +
						'type="button" class="btn btn-link" style="padding: 4px 4px!important" title="Ver historial" onclick="verFlujoEstados(\'' + row.canoAnosol + '-' + row.cdocTipdoc + '-' + row.csolNumsol + '\')">' +
						'<i class="fa-solid fa-eye fa-lg" style="color: #a91414;"></i>' +
						'</button>' +
						'<a href="/siad/alimentacion/notas-alimentos/editar/' + row.canoAnosol + '-' + row.cdocTipdoc + '-' + row.csolNumsol + '" ' +
						'class="btn btn-link" title="Editar" style="padding: 4px 4px!important">' +
						'<i class="fa-solid fa-pen-to-square fa-lg" style="color: #a91414;"></i>' +
						'</a>';

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

function buscarNotasAlimentos() {
	if (validarFormBuscarNotasAlimentos()) {
		tablaNotasAlimentos.ajax.reload();
	}
}

function validarFormBuscarNotasAlimentos() {
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

function verFlujoEstados(id) {
	$.ajax({
		url: '/siad/alimentacion/notas-alimentos/flujos/' + id,
		type: 'GET',
		processData: false,
		contentType: false,
		success: function(response) {
			$("#modalVerFlujos").html(response);
			$("#modalVerFlujos").modal("show");
		},
		error: function(xhr, status, error) {
			mostrarAlertaError(xhr, status, error);
		}
	});
}

/**********************  FUNCIONES PANTALLA REGISTRAR *********************/

function configuracionesRegistro() {

	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	});

	initTablaRaciones();

	$('#tableRaciones tbody').on('click', '.eliminarRacion', function() {
		var row = $(this).closest('tr');
		$('#tableRaciones').DataTable().row(row).remove().draw();
	});

	$('#tableRaciones tbody').on('click', '.editarRacion', function() {
		var row = $(this).closest('tr');
		selectedRow = row;
		verEditarRacion(selectedRow);
	});

	var selectOficina = document.getElementById("oficinaSolicitud");

	if (selectOficina.options.length > 0) {
		valorAntiguoSelectOficina = $("#oficinaSolicitud option:selected").val();
		buscarEmpleadosPorOficina(false, function() { });
	} else {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
	}
}

function initTablaRaciones() {
	if ($.fn.dataTable.isDataTable('#tableRaciones')) {
		$('#tableRaciones').DataTable().clear();
		$('#tableRaciones').DataTable().destroy();
	}
	tablaRaciones = $('#tableRaciones').DataTable({
		"columns": [
			{ "width": "0%" }, //NUMERO DE ITEM RACION
			{ "width": "0%" }, //CODIGO DE TIPO DE RACION
			{ "width": "5%" }, //TIPO DE RACION
			{ "width": "10%" }, //CODIGO EMPLEADO
			{ "width": "30%" }, //NOMBRES EMPLEADO
			{ "width": "10%" }, //FOTOCHECK
			{ "width": "5%" },//CANTIDAD
			{ "width": "30%" },//OBSERVACION
			{ "width": "10%" }//OPCIONES
		],
		scrollX: true,
		columnDefs: [{
			targets: "_all",
			sortable: false
		}],
		ordering: false,
		"paging": false,
		searching: false,
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
			emptyTable: "Por favor, agregar racion(es)"
		}

	});
}

function habilitarApoyo() {
	if (tablaRaciones.rows().count() > 0) {
		$('#confirmChangeApoyoModal').modal('show');
	} else {
		buscarEmpleadosPorOficina(false, function() { });
		cambioApoyo();
	}
}

function confirmarCambioApoyo() {
	tablaRaciones.clear().draw();
	buscarEmpleadosPorOficina();
	cambioApoyo();
	$('#confirmChangeApoyoModal').modal('hide');
}

function cancelarCambioApoyo() {
	if($('#apoyoSolicitud').is(":checked")){
		$('#apoyoSolicitud').prop('checked', false);
	}else{
		$('#apoyoSolicitud').prop('checked', true);
	}
	$('#confirmChangeApoyoModal').modal('hide');
}

function cambiarOficina() {
	if (tablaRaciones.rows().count() > 0) {
		$('#confirmChangeOficinaModal').modal('show');
	} else {
		valorAntiguoSelectOficina = $("#oficinaSolicitud option:selected").val();
		buscarEmpleadosPorOficina(false, function() { });
	}
}

function confirmarCambioOficina() {
	valorAntiguoSelectOficina = $("#oficinaSolicitud option:selected").val();
	tablaRaciones.clear().draw();
	buscarEmpleadosPorOficina();
	$('#confirmChangeOficinaModal').modal('hide');
}

function cancelarCambioOficina() {
	$("#oficinaSolicitud").val(valorAntiguoSelectOficina);
	$('#confirmChangeOficinaModal').modal('hide');
}

function cambioApoyo(){
	if($('#apoyoSolicitud').is(":checked")){
		$('#tituloCodigoTablaCodigo').text('Cód. Apoyo');
		$('#motivoSolicitud').prop("disabled", false);
		$('#motivoSolicitud').val('');
	}else{
		$('#tituloCodigoTablaCodigo').text('Cód. Empl.');
		$('#motivoSolicitud').prop("disabled", true);
		$('#motivoSolicitud').val('');
	}
}

function buscarEmpleadosPorOficina(callFunction, _callback) {
	var codigoOficina = $("#oficinaSolicitud option:selected").val();
	var indicadorApoyoChecked = $('#apoyoSolicitud').is(":checked");
	var indicadorApoyo = '0';
	if(indicadorApoyoChecked){
		indicadorApoyo = '1';
	}
	
	$.ajax({
		url: '/siad/rest/alimentacion/notas-alimentos/empleados/' + codigoOficina + "/" + indicadorApoyo,
		type: 'GET',
		processData: false,
		contentType: 'application/json',
		success: function(response) {
			if (response && response.length > 0) {
				var html = '';
				
				for (var i = 0; i < response.length; i++) {
					var empleado = response[i];
					var codigo = empleado.cempCodemp;
					if(indicadorApoyo=='1'){
						codigo = empleado.capoCodapo;
					}
					
					var option = '<option value="' + codigo + '-' + empleado.fotocheck + '">' + empleado.nombre + '</option>';
					html += option;
				}
				
				$("#selectEmpleadoRacion").html(html);

				if (callFunction) {
					_callback();
				}
			} else {
				$("#selectEmpleadoRacion").empty();
				toastr.info('No se encontraron empleados relacionados a la oficina', 'INFO');
			}
		},
		error: function(xhr, status, error) {
			$("#selectEmpleadoRacion").empty();
			mostrarAlertaError(xhr, status, error);
		}
	});
}

function limpiarFormRacion() {
	$('#numeroItemRacion').val('');
	$('#cantidadRacion').val('1');
	$('#observacionRacion').val('');
	var selectEmpleadoRacion = document.getElementById("selectEmpleadoRacion");
	var selectTipoRacion = document.getElementById("selectTipoRacion");
	selectTipoRacion.options[0].selected = true;
	if (selectEmpleadoRacion.options.length == 0) {
		toastr.info('No se encontraron registros relacionados a la oficina', 'INFO');
		return false;
	} else {
		selectEmpleadoRacion.options[0].selected = true;
	}
}

function verAgregarRacion() {
	if (document.getElementById("oficinaSolicitud").options.length == 0) {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
		return;
	} else if (!$("#oficinaSolicitud option:selected").val() || $("#oficinaSolicitud option:selected").val() == '') {
		toastr.info('Seleccionar la oficina', 'INFO');
		return;
	} else {
		if($('#apoyoSolicitud').is(":checked")){
			$('#cantidadRacion').prop("disabled", false);
		}else{
			$('#cantidadRacion').prop("disabled", true);
		}
		$('#titleModalAgregarRacion').text('Agregar Ración');
		$('.btnEditarRacion').hide();
		$('.btnAgregarRacion').show();
		limpiarFormRacion();
		$('#modalRacion').modal('show');
	}
}

function agregarRacionRegistro() {

	if (validarFormRacion()) {
		var tokensEmpleado = $("#selectEmpleadoRacion option:selected").val().split('-');
		var racion = {
			codigoTipoRacion: $("#selectTipoRacion option:selected").val(),
			nombreTipoRacion: $("#selectTipoRacion option:selected").text(),
			codigoEmpleado: tokensEmpleado[0],
			fotocheckEmpleado: tokensEmpleado[1],
			nombresEmpleado: $("#selectEmpleadoRacion option:selected").text(),
			cantidad: $("#cantidadRacion").val(),
			observacion: $("#observacionRacion").val()
		};

		var fila = '<tr>' +
			'<td style="text-align: center;display:none;">-1</td>' +
			'<td style="text-align: center;display:none;">' + racion.codigoTipoRacion + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.nombreTipoRacion + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.codigoEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.nombresEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.fotocheckEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.cantidad + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + racion.observacion + '</td>' +
			'<td style="text-align: center;font-size:12px;">' +
			'<button class="btn btn-xs btn-outline-primary orange-button editarRacion mr-1"' +
			'title="Editar ración">' +
			'<i class="fa-solid fa-pen"></i>' +
			'</button>' +
			'<button class="btn btn-xs btn-outline-danger eliminarRacion"' +
			'title="Quitar ración">' +
			'<i class="fa-solid fa-xmark"></i>' +
			'</button>' +
			'</td>' +
			'</tr>';
		$('#tableRaciones').DataTable().row.add($(fila));
		$('#tableRaciones').DataTable().draw();

		$('#modalRacion').modal('hide');
	}
}

function validarFormRacion() {
	if (document.getElementById("selectEmpleadoRacion").options.length == 0) {
		toastr.info('No se encontraron empleados relacionados a la oficina', 'INFO');
		return false;
	}
	if ($("#selectTipoRacion option:selected").val() == '') {
		toastr.info("Seleccionar el tipo de racion", 'INFO');
		return false;
	}
	if ($("#selectEmpleadoRacion option:selected").val() == '') {
		toastr.info("Seleccionar el empleado", 'INFO');
		return false;
	}
	var cantidad = $("#cantidadRacion").val();
	if (!cantidad || cantidad == '') {
		toastr.info("Ingresar cantidad", 'INFO');
		return false;
	} else if (isNaN(cantidad) || parseFloat(cantidad) <= 0) {
		toastr.info("Valor en cantidad no permitida", 'INFO');
		return false;
	}

	var tipoRacion = $("#selectTipoRacion option:selected").val();
	var codigoEmpleado = $("#selectEmpleadoRacion option:selected").val().split('-')[0];
	var isRepeated = false;

	if ($('#numeroItemRacion').val() == '') {
		$("#tableRaciones tbody tr").each(function() {
			if (!$(this).find("td:eq(0)").hasClass('dataTables_empty')) {
				var codigoTipoRacion = $(this).find("td:eq(1)").text();
				var codigoEmpleadoRacion = $(this).find("td:eq(3)").text();
				if (codigoTipoRacion == tipoRacion && codigoEmpleadoRacion == codigoEmpleado) {
					isRepeated = true;
				}
			}
		});
	} else {
		var codigoTipoRacionFilaSeleccionada = selectedRow.find("td:eq(1)").text();
		var codigoEmpleadoRacionFilaSeleccionada = selectedRow.find("td:eq(3)").text();
		$("#tableRaciones tbody tr").each(function() {
			if (!$(this).find("td:eq(0)").hasClass('dataTables_empty')) {
				var codigoTipoRacion = $(this).find("td:eq(1)").text();
				var codigoEmpleadoRacion = $(this).find("td:eq(3)").text();
				if (codigoTipoRacionFilaSeleccionada != codigoTipoRacion || codigoEmpleadoRacionFilaSeleccionada != codigoEmpleadoRacion) {
					if (codigoTipoRacion == tipoRacion && codigoEmpleadoRacion == codigoEmpleado) {
						isRepeated = true;
					}
				}
			}
		});
	}
	if (isRepeated) {
		toastr.info("Tipo de racion y empleado existentes", 'INFO');
		return false;
	}
	return true;
}

function registrarNotaAlimento() {
	if (validarFormRegistroAlimentos()) {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Registrando . . .');
		var fechaSolicitud = $('#fechaSolicitud').val();
		var fechaInicioSolicitud = $('#fechaInicioSolicitud').val();
		var fechaFinSolicitud = $('#fechaFinSolicitud').val();
		var codigoOficinaSolicitud = $("#oficinaSolicitud option:selected").val();
		var observacionSolicitud = $('#observacionSolicitud').val();
		var procedenciaSolicitud = $('#procedenciaSolicitud').val();
		var tipoDocumento = '310';/* SOLO TIPO DE DOCUMENTO - NOTAS DE ALIMENTOS */
		
		var codigoMotivoSolicitud = $("#motivoSolicitud option:selected").val();
		var indicadorApoyoChecked = $('#apoyoSolicitud').is(":checked");
		var indicadorApoyo = '0';
		if(indicadorApoyoChecked){
			indicadorApoyo = '1';
		}
		var raciones = [];
		var data;

		$("#tableRaciones tbody tr").each(function() {
			if (!$(this).find("td:eq(0)").hasClass('dataTables_empty')) {
				var codigoTipoRacion = $(this).find("td:eq(1)").text();
				var codigoEmpleadoRacion = $(this).find("td:eq(3)").text();
				var codigoOficinaRacion = codigoOficinaSolicitud;
				var cantidadRacion = $(this).find("td:eq(6)").text();
				var observacionRacion = $(this).find("td:eq(7)").text();

				var racion = {
					chorCodtipo: codigoTipoRacion,
					cofiCodofi: codigoOficinaRacion,
					nsodCanper: cantidadRacion,
					csodSodobs: observacionRacion
				};
				
				if(indicadorApoyo=='1'){
					racion.capoCodapo = codigoEmpleadoRacion;
				}else{
					racion.cempCodemp = codigoEmpleadoRacion;
				}

				raciones.push(racion);
			}
		});
		
		var data = {
			cdocTipdoc: tipoDocumento,
			dsolFecsol: fechaSolicitud,
			dsolFecini: fechaInicioSolicitud,
			dsolFecfin: fechaFinSolicitud,
			cofiCodofi: codigoOficinaSolicitud,
			csolObssol: observacionSolicitud,
			csolProce: procedenciaSolicitud,
			csolIndapo: indicadorApoyo,
			items: raciones
		};
		
		if(codigoMotivoSolicitud!=''){
			data.ssolMotivo = codigoMotivoSolicitud;
		}

		$.ajax({
			url: '/siad/rest/alimentacion/notas-alimentos/registrar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				var id = response.canoAnosol + '-' + response.cdocTipdoc + '-' + response.csolNumsol;
				$.ajax({
					url: '/siad/alimentacion/notas-alimentos/editar/fragment/' + id,
					type: 'GET',
					processData: false,
					contentType: false,
					success: function(responseFragment) {
						$("#fragmentContainerRegistro").html(responseFragment);
						configuracionesEditar();
						$('#modalLoading').modal('hide');
						toastr.success('Registro guardado correctamente', 'OK');
					},
					error: function(xhr, status, error) {
						$('#modalLoading').modal('hide');
						mostrarAlertaError(xhr, status, error);
					}
				});
				
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
	}

}

function validarFormRegistroAlimentos() {
	if ($("#fechaSolicitud").val() == '') {
		toastr.info("Seleccionar fecha de solicitud", 'Info');
		return false;
	}
	if ($("#oficinaSolicitud option:selected").val() == '') {
		toastr.info("Seleccionar la oficina", 'Info');
		return false;
	}
	if ($("#fechaInicioSolicitud").val() == '') {
		toastr.info("Seleccionar fecha de inicio", 'Info');
		return false;
	}
	if ($("#fechaFinSolicitud").val() == '') {
		toastr.info("Seleccionar fecha de fin", 'Info');
		return false;
	}
	if (tablaRaciones.rows().count() === 0) {
		toastr.info("Agregar racion(es)", 'Info');
		return false;
	}
	return true;
}

function editarRacionRegistro() {
	if (validarFormRacion()) {
		var tokensEmpleado = $("#selectEmpleadoRacion option:selected").val().split('-');
		
		var codigoTipoRacion = $("#selectTipoRacion option:selected").val();
		var nombreTipoRacion = $("#selectTipoRacion option:selected").text();
		var codigoEmpleado = tokensEmpleado[0];
		var fotocheckEmpleado = tokensEmpleado[1];
		var nombresEmpleado = $("#selectEmpleadoRacion option:selected").text();
		var cantidad = $("#cantidadRacion").val();
		var observacion = $("#observacionRacion").val();
		
		selectedRow.find("td:eq(1)").text(codigoTipoRacion);
		selectedRow.find("td:eq(2)").text(nombreTipoRacion);
		selectedRow.find("td:eq(3)").text(codigoEmpleado);
		selectedRow.find("td:eq(4)").text(nombresEmpleado);
		selectedRow.find("td:eq(5)").text(fotocheckEmpleado);
		selectedRow.find("td:eq(6)").text(cantidad);
		selectedRow.find("td:eq(7)").text(observacion);

		$('#modalRacion').modal('hide');
	}
}

/**********************  FUNCIONES PANTALLA EDITAR *********************/

function configuracionesEditar() {

	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	});

	initModalesEditar();
	initTablaRaciones();

	$('#tableRaciones tbody').find('.eliminarRacion').unbind('click');
	$('#tableRaciones tbody').on('click', '.eliminarRacion', function() {
		var row = $(this).closest('tr');

		var numeroItemRacion = row.find("td:eq(0)").text();
		if (numeroItemRacion == '-1') {
			for (var i = 0; i < listaAgregar.length; i++) {
				var itemAgregar = listaAgregar[i];
				if (itemAgregar.nsodItem == numeroItemRacion) {
					listaAgregar.splice(i, 1);
					break;
				}
			}
		} else {
			for (var i = 0; i < listaModificar.length; i++) {
				var itemModificar = listaModificar[i];
				if (itemModificar.nsodItem == numeroItemRacion) {
					listaModificar.splice(i, 1);
					break;
				}
			}

			var found = false;
			for (var i = 0; i < listaEliminar.length; i++) {
				var itemEliminar = listaEliminar[i];
				if (itemEliminar.nsodItem == numeroItemRacion) {
					found = true;
					break;
				}
			}

			if (!found) {
				var racion = {
					nsodItem: numeroItemRacion,
				};
				listaEliminar.push(racion);
			}
		}

		$('#tableRaciones').DataTable().row(row).remove().draw();
	});

	$('#tableRaciones tbody').find('.editarRacion').unbind('click');
	$('#tableRaciones tbody').on('click', '.editarRacion', function() {
		var row = $(this).closest('tr');
		selectedRow = row;
		verEditarRacion(selectedRow);
	});

	var selectOficina = document.getElementById("oficinaSolicitud");

	if (selectOficina.options.length > 0) {
		valorAntiguoSelectOficina = $("#oficinaSolicitud option:selected").val();
		buscarEmpleadosPorOficina(false, function() { });
	} else {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
	}

	listaAgregar = [];
	listaModificar = [];
	listaEliminar = [];
}

function verEditarRacion(selectedRow) {
	if (document.getElementById("oficinaSolicitud").options.length == 0) {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
		return;
	} else if (!$("#oficinaSolicitud option:selected").val() || $("#oficinaSolicitud option:selected").val() == '') {
		toastr.info('Seleccionar la oficina', 'INFO');
		return;
	} else {
		if($('#apoyoSolicitud').is(":checked")){
			$('#cantidadRacion').prop("disabled", false);
		}else{
			$('#cantidadRacion').prop("disabled", true);
		}
		$('#titleModalAgregarRacion').text('Editar Ración');
		$('.btnEditarRacion').show();
		$('.btnAgregarRacion').hide();
		limpiarFormRacion();
		colocarValoresFormRacion(selectedRow);
		$('#modalRacion').modal('show');
	}
}

function colocarValoresFormRacion(selectedRow) {
	var numeroItemRacion = selectedRow.find("td:eq(0)").text();
	$('#numeroItemRacion').val(numeroItemRacion);

	var codigoTipoRacion = selectedRow.find("td:eq(1)").text();
	var codigoEmpleadoRacion = selectedRow.find("td:eq(3)").text();
	var cantidadRacion = selectedRow.find("td:eq(6)").text();
	var observacionRacion = selectedRow.find("td:eq(7)").text();
	var fotocheck = selectedRow.find("td:eq(5)").text();

	$('#selectTipoRacion').val(codigoTipoRacion);
	$('#selectEmpleadoRacion').val(codigoEmpleadoRacion + "-" + fotocheck);
	$('#cantidadRacion').val(cantidadRacion);
	$('#observacionRacion').val(observacionRacion);
}

function initModalesEditar() {

	var anioSolicitud = $('#anioSolicitud').val();
	var tipoDocumentoSolicitud = $('#tipoDocumentoSolicitud').val();
	var numeroSolicitud = $('#numeroSolicitud').val();

	var data = {
		canoAnosol: anioSolicitud,
		cdocTipdoc: tipoDocumentoSolicitud,
		csolNumsol: numeroSolicitud
	};
	
	$('#confirmDeleteModal').find('.delete-confirm-button').unbind('click');
	$('#confirmDeleteModal').find('.delete-confirm-button').click(function() {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Cargando . . .');
		$.ajax({
			url: '/siad/rest/alimentacion/notas-alimentos/anular',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				toastr.success('Registro anulado correctamente', 'OK');
				deshabilitarFormEditar();
				$('#spanEstadoNota').text('ANULADO');
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
		$('#confirmDeleteModal').modal('hide'); // Ocultar el modal de confirmación
	});

	$('#confirmApproveModal').find('.approve-confirm-button').unbind('click');
	$('#confirmApproveModal').find('.approve-confirm-button').click(function() {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Cargando . . .');
		$.ajax({
			url: '/siad/rest/alimentacion/notas-alimentos/aprobar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				toastr.success('Registro aprobado correctamente', 'OK');
				deshabilitarFormEditar();
				$('#spanEstadoNota').text('APROBADO');
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
		$('#confirmApproveModal').modal('hide'); // Ocultar el modal de confirmación
	});

}

function editarNotaAlimento() {
	if (validarFormRegistroAlimentos()) {
		$('#modalLoading').modal('show');
		$('#messageModalLoading').text('Guardando . . .');
		var anioSolicitud = $('#anioSolicitud').val();
		var tipoDocumentoSolicitud = $('#tipoDocumentoSolicitud').val();
		var numeroSolicitud = $('#numeroSolicitud').val();
		var fechaSolicitud = $('#fechaSolicitud').val();
		var fechaInicioSolicitud = $('#fechaInicioSolicitud').val();
		var fechaFinSolicitud = $('#fechaFinSolicitud').val();
		var codigoOficinaSolicitud = $("#oficinaSolicitud option:selected").val();
		var observacionSolicitud = $('#observacionSolicitud').val();
		var procedenciaSolicitud = $('#procedenciaSolicitud').val();
		var codigoMotivoSolicitud = $("#motivoSolicitud option:selected").val();
		
		var indicadorApoyoChecked = $('#apoyoSolicitud').is(":checked");
		var indicadorApoyo = '0';
		if(indicadorApoyoChecked){
			indicadorApoyo = '1';
		}

		var data = {
			canoAnosol: anioSolicitud,
			cdocTipdoc: tipoDocumentoSolicitud,
			csolNumsol: numeroSolicitud,
			dsolFecsol: fechaSolicitud,
			dsolFecini: fechaInicioSolicitud,
			dsolFecfin: fechaFinSolicitud,
			cofiCodofi: codigoOficinaSolicitud,
			csolObssol: observacionSolicitud,
			csolProce: procedenciaSolicitud,
			csolIndapo: indicadorApoyo,
			listaAgregar: listaAgregar,
			listaModificar: listaModificar,
			listaEliminar: listaEliminar
		};
		
		if(codigoMotivoSolicitud!=''){
			data.ssolMotivo = codigoMotivoSolicitud;
		}

		$.ajax({
			url: '/siad/rest/alimentacion/notas-alimentos/editar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				limpiarArreglos();
				var id = response.canoAnosol + '-' + response.cdocTipdoc + '-' + response.csolNumsol;
				$.ajax({
					url: '/siad/alimentacion/notas-alimentos/editar/fragment/' + id,
					type: 'GET',
					processData: false,
					contentType: false,
					success: function(responseFragment) {
						$("#fragmentContainerRegistro").html(responseFragment);
						configuracionesEditar();
						$('#modalLoading').modal('hide');
						toastr.success('Registro modificado correctamente', 'OK');
					},
					error: function(xhr, status, error) {
						$('#modalLoading').modal('hide');
						mostrarAlertaError(xhr, status, error);
					}
				});
				
			},
			error: function(xhr, status, error) {
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		});
	}
}


function limpiarArreglos(){
	listaAgregar = [];
	listaModificar = [];
	listaEliminar = [];
}

function agregarRacionEditar() {

	if (validarFormRacion()) {
		var tokensEmpleado = $("#selectEmpleadoRacion option:selected").val().split('-');
		
		var codigoTipoRacion = $("#selectTipoRacion option:selected").val();
		var nombreTipoRacion = $("#selectTipoRacion option:selected").text();
		var codigoEmpleado = tokensEmpleado[0];
		var fotocheckEmpleado = tokensEmpleado[1];
		var nombresEmpleado = $("#selectEmpleadoRacion option:selected").text();
		var cantidad = $("#cantidadRacion").val();
		var observacion = $("#observacionRacion").val();
		
		var indicadorApoyoChecked = $('#apoyoSolicitud').is(":checked");
		var indicadorApoyo = '0';
		if(indicadorApoyoChecked){
			indicadorApoyo = '1';
		}
		
		var racion = {
			nsodItem: '-1',
			chorCodtipo: codigoTipoRacion,
			nsodCanper: cantidad,
			csodSodobs: observacion
		};
		
		if(indicadorApoyo=='1'){
			racion.capoCodapo = codigoEmpleado;
		}else{
			racion.cempCodemp = codigoEmpleado;
		}
		

		listaAgregar.push(racion);

		var fila = '<tr>' +
			'<td style="text-align: center;display:none;">-1</td>' +
			'<td style="text-align: center;display:none;">' + codigoTipoRacion + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + nombreTipoRacion + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + codigoEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + nombresEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + fotocheckEmpleado + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + cantidad + '</td>' +
			'<td style="text-align: center;font-size:13px;" class="py-0">' + observacion + '</td>' +
			'<td style="text-align: center;font-size:12px;">' +
			'<button class="btn btn-xs btn-outline-primary orange-button editarRacion mr-1"' +
			'title="Editar ración">' +
			'<i class="fa-solid fa-pen"></i>' +
			'</button>' +
			'<button class="btn btn-xs btn-outline-danger eliminarRacion"' +
			'title="Quitar ración">' +
			'<i class="fa-solid fa-xmark"></i>' +
			'</button>' +
			'</td>' +
			'</tr>';
		$('#tableRaciones').DataTable().row.add($(fila));
		$('#tableRaciones').DataTable().draw();

		$('#modalRacion').modal('hide');
	}
}

function editarRacionEditar() {
	if (validarFormRacion()) {
		var tokensEmpleado = $("#selectEmpleadoRacion option:selected").val().split('-');
		
		var codigoTipoRacion = $("#selectTipoRacion option:selected").val();
		var nombreTipoRacion = $("#selectTipoRacion option:selected").text();
		var codigoEmpleado = tokensEmpleado[0];
		var fotocheckEmpleado = tokensEmpleado[1];
		var nombresEmpleado = $("#selectEmpleadoRacion option:selected").text();
		var cantidad = $("#cantidadRacion").val();
		var observacion = $("#observacionRacion").val();
		
		var indicadorApoyoChecked = $('#apoyoSolicitud').is(":checked");
		var indicadorApoyo = '0';
		if(indicadorApoyoChecked){
			indicadorApoyo = '1';
		}

		var numeroItemRacion = selectedRow.find("td:eq(0)").text();
		if (numeroItemRacion != '-1') {
			for (var i = 0; i < listaModificar.length; i++) {
				var itemModificar = listaModificar[i];
				if (itemModificar.nsodItem == numeroItemRacion) {
					listaModificar.splice(i, 1);
					break;
				}
			}
			
			var racion = {
				nsodItem: numeroItemRacion,
				chorCodtipo: codigoTipoRacion,
				nsodCanper: cantidad,
				csodSodobs: observacion
			};
			
			if(indicadorApoyo=='1'){
				racion.capoCodapo = codigoEmpleado;
			}else{
				racion.cempCodemp = codigoEmpleado;
			}
			
			listaModificar.push(racion);
		}else{
			for (var i = 0; i < listaAgregar.length; i++) {
				var itemAgregar = listaAgregar[i];
				if(indicadorApoyo=='1'){
					if (itemAgregar.chorCodtipo == selectedRow.find("td:eq(1)").text() && itemAgregar.capoCodapo == selectedRow.find("td:eq(3)").text()) {
						var racion = {
							nsodItem: '-1',
							chorCodtipo: codigoTipoRacion,
							capoCodapo: codigoEmpleado,
							nsodCanper: cantidad,
							csodSodobs: observacion
						};
				
						listaAgregar[i] = racion;
						break;
					}
				}else{
					if (itemAgregar.chorCodtipo == selectedRow.find("td:eq(1)").text() && itemAgregar.cempCodemp == selectedRow.find("td:eq(3)").text()) {
						var racion = {
							nsodItem: '-1',
							chorCodtipo: codigoTipoRacion,
							cempCodemp: codigoEmpleado,
							nsodCanper: cantidad,
							csodSodobs: observacion
						};
				
						listaAgregar[i] = racion;
						break;
					}
				}
				
			}
		}
		
		selectedRow.find("td:eq(1)").text(codigoTipoRacion);
		selectedRow.find("td:eq(2)").text(nombreTipoRacion);
		selectedRow.find("td:eq(3)").text(codigoEmpleado);
		selectedRow.find("td:eq(4)").text(nombresEmpleado);
		selectedRow.find("td:eq(5)").text(fotocheckEmpleado);
		selectedRow.find("td:eq(6)").text(cantidad);
		selectedRow.find("td:eq(7)").text(observacion);

		$('#modalRacion').modal('hide');
	}
}

function deshabilitarFormEditar(){
	$('#numeroSolicitud').prop("disabled", true);
	$('#fechaSolicitud').prop("disabled", true);
	$('#fechaInicioSolicitud').prop("disabled", true);
	$('#fechaFinSolicitud').prop("disabled", true);
	$('#oficinaSolicitud').prop("disabled", true);
	$('#observacionSolicitud').prop("disabled", true);
	$('#procedenciaSolicitud').prop("disabled", true);
	$("#tableRaciones tbody tr").each(function() {
		if (!$(this).find("td:eq(0)").hasClass('dataTables_empty')) {
			$(this).find("td:eq(8)").html('');
		}
	});
	$('#btnModalAgregarRacion').hide();
	$('#btnAnularNota').hide();
	$('#btnAprobarNota').hide();
	$('#btnGuardarNotaEditar').hide();
}

/**********************  FUNCIONES GENERICAS *********************/
function editarRacion(){
	if($('#tipoTransaccion').val()=='REGISTRO'){
		editarRacionRegistro();
	}else if($('#tipoTransaccion').val()=='EDITAR'){
		editarRacionEditar();
	}
}

function agregarRacion(){
	if($('#tipoTransaccion').val()=='REGISTRO'){
		agregarRacionRegistro();
	}else if($('#tipoTransaccion').val()=='EDITAR'){
		agregarRacionEditar();
	}
}



/**********************  FUNCIONES MENU SEMANAL  *********************/




