var tablaTardanzas;

function configuraciones() {
	
	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	})
	
	inicializarTabla();

	var fechaFin = new Date().toISOString().slice(0, 10);
	var fechaInicio = new Date();
	
	fechaInicio.setDate(1);

	$('#fechaInicio').val(fechaInicio.toISOString().slice(0, 10));
//	$('#fechaInicio').val('2023-01-01');
	$('#fechaFin').val(fechaFin);
	

	var selectOficina = document.getElementById("selectOficina");

    if (selectOficina.options.length > 0) {
        buscarEmpleadosPorOficina(true,function(){
			buscarTardanzas();
		});
    }else{
		toastr.info('No se encontraron oficinas relacionadas al módulo','INFO');
		$('#modalLoading').modal('hide');
	}
}

function buscarEmpleadosPorOficina(callFunction,_callback){
	var codigoOficina = $("#selectOficina option:selected").val();
    $.ajax({
		url: '/siad/rest/asistencia/tardanzas/empleados/' + codigoOficina,
		type: 'GET',
		processData: false,
		contentType: 'application/json',
		success: function(response) {
			if (response && response.length > 0){
				var html = '';
				for (var i = 0; i < response.length; i++) {
					var empleado = response[i];
					var option = '<option value="' + empleado.CODIGO + '">' + empleado.NOMBRE + '</option>';
					html += option;
				}
				$("#selectEmpleado").html(html);
				
				if(callFunction){
					_callback();
				}
			}else{
				$("#selectEmpleado").empty();
				toastr.info('No se encontraron empleados relacionados a la oficina','INFO');
			}
			
		},
		error: function(xhr, status, error) {
			$("#selectEmpleado").empty();
			mostrarAlertaError(xhr, status, error);
		}
	});
}

function inicializarTabla(){
	tablaTardanzas = $('#tableTardanzas').DataTable({
		columnDefs:[{
            targets: "_all",
            sortable: false
        }],
        ordering: false,
		processing: false,
		"deferLoading": 0,
		serverSide: true,
		"info": true,
		ajax: {
			url: '/siad/rest/asistencia/tardanzas/buscar',
			method: 'POST',
			contentType: 'application/json',
			data: function(d) {

				var codigoEmpleado = $("#selectEmpleado option:selected").val();
				
				var fechaInicio = $('#fechaInicio').val();
				var fechaFin = $('#fechaFin').val();

				var data = {
					codigoEmpleado: codigoEmpleado,
					fechaInicio: fechaInicio,
					fechaFin: fechaFin,
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
				$('#modalLoading').modal('hide');
				mostrarAlertaError(xhr, status, error);
			}
		},
		"columns": [
			{ "width": "20%", "data": "cempCodemp" }, 
			{ "width": "30%", "data": "nombreEmpleado" }, 
			{ "width": "10%", "data": "dia" }, 
			{ "width": "10%", "data": "fingreso" }, 
			{ "width": "15%", "data": "hingreso" }, 
			{ "width": "15%", "data": "ingreso" }
		],
		columnDefs: [
			{
				targets: [0, 1, 2, 3, 4, 5],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
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
function buscarTardanzas() {
	if(validarFormBuscarTardanzas()){
		tablaTardanzas.ajax.reload();
	}
}

function validarFormBuscarTardanzas(){
    if (document.getElementById("selectOficina").options.length == 0) {
        toastr.info('No se encontraron oficinas relacionadas al módulo','INFO');
        return false;
    }
    if (document.getElementById("selectEmpleado").options.length == 0) {
        toastr.info('No se encontraron empleados relacionados a la oficina','INFO');
        return false;
    }
	
	if($("#selectOficina option:selected").val() == ''){
		toastr.info("Seleccionar la oficina", 'INFO');
		return false;
	}
	if($("#selectEmpleado option:selected").val() == ''){
		toastr.info("Seleccionar el empleado", 'INFO');
		return false;
	}
	return true;
}


