$(function() {
	showMessage();
});

function showMessage() {
	if ($('#messageSuccess') && $('#messageSuccess').val()) {
		toastr.success($('#messageSuccess').val(), 'OK');
	}
	if ($('#messageError') && $('#messageError').val()) {
		toastr.error($('#messageError').val(), 'Error');
	}
	if ($('#messageInfo') && $('#messageInfo').val()) {
		toastr.info($('#messageInfo').val(), 'Info');
	}
	if ($('#messageWarning') && $('#messageWarning').val()) {
		toastr.warn($('#messageWarning').val(), 'Advertencia');
	}
	if ($('#messageSystem') && $('#messageSystem').val()) {
		toastr.success($('#messageSystem').val(), 'OK');
	}
}

function mostrarAlertaError(xhr,status,error){
	console.log(xhr);
	console.log(xhr.status);
	console.log(status);
	if(xhr.status && xhr.status == 403){ /* ACCESO DENEGADO */
			toastr.warning('<ul><li style="padding-left: 1em;"><i class="fas fa-exclamation-circle mr-2"></i>Acceso Denegado</li></ul>', 'Alerta');
	}else if(xhr.status && xhr.status == 400){ /* BAD REQUEST */
		var responseData = JSON.parse(xhr.responseText);
		var errors = '';
		if(responseData && responseData.length > 1){
			$.each(responseData, function(index, value) {
				errors += '<li style="padding-left: 1em;"><i class="fas fa-exclamation-circle mr-2"></i> ' + value + '</li>';
			});
		}else{
			$.each(responseData, function(index, value) {
				errors += '<li style="padding-left: 1em;">' + value + '</li>';
			});
		}
		toastr.info('<ul>' + errors + '</ul>', 'Alerta');
	}else if (xhr.status && xhr.status == 500) { /* EXCEPCION DEL SISTEMA */
		if(xhr.responseJSON && xhr.responseJSON.message){
			toastr.error(xhr.responseJSON.message, 'Error');
		}else if(xhr.responseJSON && Array.isArray(xhr.responseJSON) && xhr.responseJSON[0]){
			toastr.error(xhr.responseJSON[0], 'Error');
		}else{
			toastr.error('Error en el sistema. Consultar con el administrador.', 'Error');
		}
	} else { /* MENSAJE DEFAULT */
		toastr.error('Error en el sistema. Consultar con el administrador.', 'Error');
	}
}

function obtenerAnios(){
	$('#modalLoading').modal('show');
	$.ajax({
		url: '/siad/rest/exportar-data/reporte/listar/anios',
		type: 'GET',
		contentType: 'application/json',
		success: function(response) {
			
			if(response && response.length>0){
				var lista = '<ol id="listaFirmantes" class="list-group col-sm-12" style="height:450px;">';
				for(var i=0; i<response.length; i++){
					var item = response[i];
					var fila = '<li class="list-group-item d-flex justify-content-between align-items-start liOficina" style="cursor:pointer;" onclick="cambiarAnio(\'' + item.codigo + '\')">' +
			    					'<div class="ms-2 me-auto">' +
			      						'<div class="font-weight-bold">' + item.codigo + '</div>' + 
			    					'</div>' +
		  						'</li>'
		  			lista += fila;
				}
				lista += '</ol>';
				$("#listaAniosDisponibles").html(lista);
			}else{
				var lista = '<div><h5>No se encontraron años disponibles</h5></div>'
				$("#aniosModal .modal-body").html(lista);
			}
            
            $('#modalLoading').modal('hide');
	        $('#aniosModal').modal('show');
	        
		},
		error: function(xhr, status, error) {
			$('#modalLoading').modal('hide');
			mostrarAlertaError(xhr, status, error);
		}
	});
}

function cambiarAnio(codigo){
	$('#modalLoading').modal('show');
	$.ajax({
		url: '/siad/rest/principal/cambiarAnio/' + codigo,
		type: 'PUT',
		contentType: 'application/json',
		success: function(response) {
			$('#messageAnio').val("Se ha cambiado de año correctamente");
			document.getElementById("formMainAnio").submit();
		},
		error: function(xhr, status, error) {
			$('#modalLoading').modal('hide');
			mostrarAlertaError(xhr, status, error);
		}
	});
}

/*
function cerrarSesion(){
	$.ajax({
		url: '/siad/rest/principal/logout',
		type: 'GET',
		contentType: 'application/json',
		success: function(response) {
			//window.location.replace("");
			//window.close();
//			window.top.close();
			var win = window.open("about:blank", "_self");
			win.close();
		},
		error: function(xhr, status, error) {
			$('#modalLoading').modal('hide');
			mostrarAlertaError(xhr, status, error);
		}
	});
}
*/

toastr.options = {
	"closeButton": true,
	"debug": false,
	"newestOnTop": true,
	"progressBar": true,
	"positionClass": "toast-top-right",
	"preventDuplicates": true,
	"onclick": null,
	"showDuration": "10000",
	"hideDuration": "1000",
	"timeOut": "10000",
	"extendedTimeOut": "10000",
	"showEasing": "swing",
	"hideEasing": "linear",
	"showMethod": "fadeIn",
	"hideMethod": "fadeOut"
}