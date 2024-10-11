var tablaAccesoAlimentos;

function configuraciones() {

	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	});
	
	verLeyenda();
	
	inicializarTablaRawData();

	$('#txtEmpleado').val('');
	var fechaFin = new Date().toISOString().slice(0, 10);
	$('#fechaInicio').val(fechaFin);	
	
	var selectOficina = document.getElementById("selectOficina");
	
	if (selectOficina.options.length > 0) {
		buscarAccesoAlimentos();
	} else {
		toastr.info('No se encontraron oficinas relacionadas al módulo', 'INFO');
	}
	
}

function verLeyenda() {
    var divLeyenda;
    $("#btnShowLeyenda")
            .mouseover(function() {
                var pos = $(this).position();
                //var zindex = $(this).zIndex();
                divLeyenda = $("#divLeyenda").clone().insertAfter(this).css({
                    position: "absolute",
                    top: (pos.top + 37) + "px",
                    left: (pos.left + 50) + "px",
                    "z-index": 1,
                    height: 70 + "px",
    				width: 170 + "px"
                });
                
                $("#sectionTable").css({
                    "z-index": -1
                });

                divLeyenda.show();
            })
            
            .mouseleave(function() {
				$("#sectionTable").css({
                    "z-index": 1
                });
                
                if (!!divLeyenda) {
                    divLeyenda.remove();
                }
            });
            
            
}

function validarFormBuscarAccesoAlimentos() {
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

function inicializarTablaRawData() {
	tablaAccesoAlimentos = $('#tableAccesoAlimentos').DataTable({
		columnDefs: [{
			targets: "_all",
			sortable: false
		}],
		ordering: false,
		"columns": [
			{ "width": "10%", "data": "CODIGOEMP" },
			{ "width": "25%", "data": "APENOMEMP" },
			{ "width": "30%", "data": "OFICINAEMP" },
			{ "width": "10%", "data": "FOTOCHKEMP" },
			{ "width": "5%", "data": "DESAYUNO" },
			{ "width": "5%", "data": "ALMUERZO" },
			{ "width": "5%", "data": "CENA" },
			{ "width": "10%", "data": "ESTADO"}
		],
		columnDefs: [
			{
				targets: [0, 1, 2, 3],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
			{
				targets: 4,
				render: function(data, type, row, meta) {
					var columna = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">';
					if(row.DESAYUNO == 1){
						columna += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick" checked/>';
					}else{
						columna += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick">';
					}
					columna += '</div>';
					return columna;
				}
			},
			{
				targets: 5,
				render: function(data, type, row, meta) {
					var output = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">';
					if(row.ALMUERZO == 1){
						output += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick" checked/>';
					}else{
						output += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick"/>';
					}
					output += '</div>';
					return output;
				}
			},
			{
				targets: 6,
				render: function(data, type, row, meta) {
					var output = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">';
					if(row.CENA == 1){
						output += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick" checked/>';
					}else{
						output += '<input type="checkbox" class="form-check-label checkbox-large checkboxNoClick"/>';
					}
					output += '</div>';
					return output;
				}
			},
			{
				targets: 7,
				render: function(data, type, row, meta) {
					var output = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">';
					if(row.ESTADO == 1){
						output += '<div class="badge p-2" style="background-color: rgb(252 231 243); color: rgb(157 23 77);">Baja</div>';
					}else{
						output += '<div class="badge p-2" style="background-color: rgb(220 252 231); color: rgb(22 101 52);">Activo</div>';
					}
					output += '</div>';
					return output;
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

function buscarAccesoAlimentos() {
	if(validarFormBuscarAccesoAlimentos()){
		var fechaInicio = $('#fechaInicio').val();
		var codigoOficina = $("#selectOficina option:selected").val();
		var empleado = $('#txtEmpleado').val();
		var codigoEstado = $("#selectEstado option:selected").val();

		var data = {
			fecha: fechaInicio,
			codigoOficina: codigoOficina,
			empleado: empleado,
			estado: codigoEstado
		}

		$.ajax({
			url: '/siad/rest/alimentacion/acceso-alimentos/buscar',
			type: 'POST',
			data: JSON.stringify(data),
			processData: false,
			contentType: 'application/json',
			success: function(response) {
				$('#modalLoading').modal('hide');
				tablaAccesoAlimentos.clear().draw();
			   	tablaAccesoAlimentos.rows.add(response); 
			   	tablaAccesoAlimentos.columns.adjust().draw();
			},
			beforeSend: function() {
				$('#modalLoading').modal('show');
			},
			complete: function() {
				$('#modalLoading').modal('hide');
				$('.checkboxNoClick').unbind('click');
				$('.checkboxNoClick').on('click', function() {
					return false;
				});
			},
			error: function(xhr, status, error) {
				mostrarAlertaError(xhr, status, error);
			}
		});
	}
}
