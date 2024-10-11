var tablaReportes;

/**********************  FUNCIONES PANTALLA PRINCIPAL *********************/

function configuraciones() {

	$('#modalLoading').on('hidden.bs.modal', function(event) {
		$('#messageModalLoading').text('Cargando . . .');
	});

	inicializarTabla();

	buscarReportes();
}

function inicializarTabla() {
	tablaReportes = $('#tableReportes').DataTable({
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
			url: '/siad/rest/exportar-data/buscar',
			method: 'POST',
			contentType: 'application/json',
			data: function(d) {

				var descripcion = $('#txtDescripcion').val();

				var data = {
					descripcion: descripcion,
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
			{ "width": "10%", "data": "ID" },
			{ "width": "80%", "data": "DESCRIPCION" },
			{ "width": "10%" }//OPCIONES
		],
		columnDefs: [
			{
				targets: [0],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;font-size: 13px;">' + data + '</div>';
				}
			},
			{
				targets: [1],
				render: function(data, type, row, meta) {
					return '<div style="height: 100%;text-align: left;display: flex;align-items: center;justify-content: left;font-size: 13px;">' + data + '</div>';
				}
			},
			{
				targets: 2,
				render: function(data, type, row, meta) {
					var columna = '<div style="height: 100%;text-align: center;display: flex;align-items: center;justify-content: center;">';
					columna += '<button ' +
						'type="button" class="btn btn-link" style="padding: 4px 4px!important" title="Exportar" onclick="verModalExportar(\'' + row.ID + '\')">' +
						'<i class="fa-solid fa-file-export fa-lg" style="color: #a91414;"></i>' +
						'</button>'
						;

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

function buscarReportes() {
	tablaReportes.ajax.reload();
}

function verModalExportar(id) {
	$('#modalLoading').modal('show');
	$.ajax({
		url: '/siad/exportar-data/reporte/' + id,
		type: 'GET',
		processData: false,
		contentType: false,
		success: function(response) {
			$('#modalLoading').modal('hide');
			$("#modalVerReporte").html(response);
			$("#modalVerReporte").modal("show");
			fn_inicializarSelect2();
		},
		error: function(xhr, status, error) {
			$('#modalLoading').modal('hide');
			mostrarAlertaError(xhr, status, error);
		}
	});
}

function generarReporte() {
	$('#messageModalLoading').text('Generando reporte . . .');
	$('#modalLoading').modal('show');
	
	var data = {
		id: $('#correlativo').val(),
		p1: $("#selectAnio").val(),
		p4: $("#selectOficina").val(),
		p5: $("#selectGrupo").val(),
		p6: $("#selectClase").val(),
		p7: $("#selectArticulo").val(),
		p8: $("#fechaDesde").val(),
		p9: $("#fechaHasta").val(),
		px: $("#txtOtro").val(),
	};
		
	$.ajax({
		url: '/siad/rest/exportar-data/reporte/generar',
		type: 'POST',
		data: JSON.stringify(data),
		processData: false,
		contentType: 'application/json',
		success: function(response) {
			$('#modalLoading').modal('hide');
			const byteCharacters = atob(response.archivo);
			const byteNumbers = new Array(byteCharacters.length);
			for (let i = 0; i < byteCharacters.length; i++) {
				byteNumbers[i] = byteCharacters.charCodeAt(i);
			}

			var bytes = new Uint8Array(byteNumbers);
			var nombreArchivo = response.nombre;
			var file = new Blob([bytes], { type: "application/xlsx" });

			let url = window.URL.createObjectURL(file);
			let a = document.createElement('a');
			document.body.appendChild(a);
			a.setAttribute('style', 'display: none');
			a.href = url;
			a.download = nombreArchivo;
			a.click();
			window.URL.revokeObjectURL(url);
			a.remove();
		},
		error: function(xhr, status, error) {
			$('#modalLoading').modal('hide');
			mostrarAlertaError(xhr, status, error);
		}
	});
}

function fn_inicializarSelect2(){
    $('#selectGrupo').select2({
        placeholder: 'Buscar..',
        language: 'es',
        ajax: {
            url: '/siad/rest/exportar-data/reporte/buscar/grupos',
		    data: function (params) {
              var query = {
                termino: params.term
              }
              return query;
            },
            dataType: 'json',
            type: "GET",
            delay: 350,
            cache: true,
            processResults: function (data) {
                return {
                  results: $.map(data, function(item){
					 return {
						text: item.CGRU_DESGRU,
						id: item.CGRU_CODGRU
					 } 
				  })
                };
            },
            error: function (xhr, status, error) {
		        toastr.warning('No se pueden cargar los grupos en este momento');
		        return { results: [] };
		    }
        },
        allowClear: true
    });
    
    $('#selectGrupo').on("select2:select", function(e) { 
        jQuery('#codgru').val(e.params.data.id);
        $('#selectClase').val(null).trigger("change");
    });
    
    $('#selectGrupo').on("select2:clear", function(e) { 
        jQuery('#codgru').val('');
        $('#selectClase').val(null).trigger("change");
    });

    
    $('#selectArticulo').select2({
        placeholder: 'Buscar..',
        language: 'es',
        ajax: {
            url: '/siad/rest/exportar-data/reporte/buscar/articulos',
		    data: function (params) {
              var query = {
                termino: params.term
              }
              return query;
            },
            dataType: 'json',
            type: "GET",
            delay: 350,
            cache: true,
            processResults: function (data) {
                return {
                  results: $.map(data, function(item){
					 return {
						text: item.CART_DESART,
						id: item.CODART
					 } 
				  })
                };
            },
            error: function (xhr, status, error) {
		        toastr.warning('No se pueden cargar los articulos en este momento');
		        return { results: [] };
		    }
        },
        allowClear: true
    });
    
    $('#selectClase').select2({
        placeholder: 'Buscar..',
        language: 'es',
        ajax: {
            url: '/siad/rest/exportar-data/reporte/buscar/clases',
		    data: function (params) {
              var query = {
                termino: params.term,
                codgru: $('#codgru').val()
              }
              return query;
            },
            dataType: 'json',
            type: "GET",
            delay: 350,
            cache: true,
            processResults: function (data) {
                return {
                  results: $.map(data, function(item){
					 return {
						text: item.CDES_CLASE,
						id: item.CCLA_CODCLA
					 } 
				  })
                };
            },
            error: function (xhr, status, error) {
		        toastr.warning('No se pueden cargar las clases en este momento');
		        return { results: [] };
		    }
        },
        allowClear: true
    });
    
    $('#selectClase').on("select2:opening", function(e) { 
        if(jQuery('#codgru').val() == ''){
			e.preventDefault();
			toastr.info('Seleccionar el grupo', 'Alerta');
		}
    });
    
    $.ajax({
		url: '/siad/rest/exportar-data/reporte/listar/oficinas',
		type: 'GET',
		processData: false,
		contentType: false,
		success: function(response) {
			$('#selectOficina').select2({
		        placeholder: 'Buscar..',
		        language: 'es',
		        allowClear: true,
		        data: $.map(response, function(item){
					 return {
						text: item.NOMBRE,
						id: item.CODIGO
					 } 
				  })
		    });
		    $('#selectOficina').val(null).trigger("change");
		},
		error: function(xhr, status, error) {
			toastr.warning('No se pueden cargar las oficinas en este momento');
		}
	});
	
	$.ajax({
		url: '/siad/rest/exportar-data/reporte/listar/anios',
		type: 'GET',
		processData: false,
		contentType: false,
		success: function(response) {
			$('#selectAnio').select2({
		        placeholder: 'Buscar..',
		        language: 'es',
		        allowClear: true,
		        data: $.map(response, function(item){
					 return {
						text: item.codigo,
						id: item.codigo
					 } 
				  })
		    });
		    $('#selectAnio').val(null).trigger("change");
		},
		error: function(xhr, status, error) {
			toastr.warning('No se pueden cargar los años en este momento');
		}
	});
    
}
