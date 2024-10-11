
/**********************  FUNCIONES MENU SEMANAL  *********************/

function actualizarSemanas() {
	var anio = $("#selectAnio").val();

	$.ajax({
		url: '/siad/rest/alimentacion/menu-semanal/semanas/' + anio,
		type: 'GET',
		dataType: 'json',
		success: function(response) {
			if (response && response.length > 0) {
				var html = '';
				for (var i = 0; i < response.length; i++) {
					var semana = response[i];
					var option = '<option value="' + semana.codigo + '">' + semana.nombre + '</option>';
					html += option;
				}
				$("#selectSemana").html(html);
			} else {
				$("#selectSemana").empty();
				toastr.info('No se encontraron semanas relacionadas al año', 'INFO');
			}
		},
		error: function(xhr, status, error) {
			$("#selectSemana").empty();

			mostrarAlertaError(xhr, status, error);

		}
	});
}

$(document).ready(function() {
	$("#selectAnio").change(function() {
		actualizarSemanas();
		$("#CtnListaDias").hide();
		$("#CtnListaAlimentos").hide();
	});
});

/* BACKUP LISTARDIAS()

function listarDias() {
	var anio = document.getElementById("selectAnio").value;
	var numSemana = document.getElementById("selectSemana").value;
	var pk = anio + '-' + numSemana;

	var data = {
		pk: pk
	};
	
	$.ajax({
        url: '/siad/rest/alimentacion/menu-semanal/buscar',
        type: "POST",
        contentType: "application/json",
        data: pk,
        success: function(response) {
            $('#listaDias').empty();
            // Iterar sobre las claves (nombres de los días) del objeto response
            response.forEach(function(item) {
                var nombreDia = item.nombreDia;
                var fecha = item.fecha;
                $('#listaDias').append('<li class="list-group-item">' +
										    '<div class="row">' +
										        '<div class="form-group col-lg-9 my-2">' +
										            '<label>' + nombreDia + '</label><br>' + fecha +
										        '</div>' +
										        '<div class="form-group col-lg-3 my-2" style="text-align: -webkit-left; display: flex; align-items: center; font-size: 24px; padding-left: 10px;padding-right: 10px;">' +
										            '<i class="fa-solid fa-play"></i>' +
										        '</div>' +
										    '</div>' +
										'</li>');
            });
        },
        error: function(e) {
            console.log("ERROR: ", e);
        }
    });
}

 */


function listarDias() {
	$('#modalLoading').modal('show');
	var anio = document.getElementById("selectAnio").value;
	var numSemana = document.getElementById("selectSemana").value;
	var pk = anio + '-' + numSemana;

	var data = {
		pk: pk
	};
	
	$.ajax({
        url: '/siad/rest/alimentacion/menu-semanal/buscar',
        type: "POST",
        contentType: "application/json",
        data: pk,
        success: function(response) {
            $('#listaDias').empty();
            response.forEach(function(item,index) {
                var nombreDia = item.nombreDia;
                var fecha = item.fecha;
                $('#listaDias').append('<li class="list-group-item item-dia-menu py-1" onclick="listarMenuSemanal(\''+fecha+'\',this)" title="Ver Menú">' +
										    '<div class="row">' +
										        '<div class="form-group col-lg-9 my-2">' +
										            '<label>' + nombreDia + '</label><br>' + fecha +
										        '</div>' +
										        '<div class="form-group col-lg-3 my-2" style="text-align: -webkit-left; display: flex; align-items: center; font-size: 24px; padding-left: 10px;padding-right: 10px;">' +
										            '<button type="button" class="btn btn-link" style="padding: 4px 4px!important"> <i class="fa-solid fa-play fa-lg" style="color: #a91414;"></i></button>'+
										        '</div>' +
										    '</div>' +
										'</li>');
				
				$("#CtnListaDias").show();
				$("#CtnListaAlimentos").hide();
				$('#modalLoading').modal('hide');
            });
            
            if($('#listaDias').find('li').length>0){
				$('#listaDias').find('li')[0].click();
			}
        },
        error: function(xhr, status, error) {
            mostrarAlertaError(xhr, status, error);
        }
    });
}


function listarMenuSemanal(fecha,objeto) {
	
	var menuActive = document.querySelectorAll(".item-dia-menu-active");

	for(var i=0; i<menuActive.length; i++){
	    var item = menuActive[i]
	    item.classList.remove("item-dia-menu-active");
	}
	
	objeto.classList.add("item-dia-menu-active");
	
	$('#modalLoading').modal('show');
    var anio = document.getElementById("selectAnio").value;
    var numSemana = document.getElementById("selectSemana").value;
    var pk = anio + '-' + numSemana + '-' + fecha;

    var data = {
        pk: pk
    };
    
    $.ajax({
        url: '/siad/rest/alimentacion/menu-semanal/listar',
        type: "POST",
        contentType: "application/json",
        data: pk,
        success: function(response) {
            $('#listaDesayuno').empty();
            $('#listaAlmuerzo').empty();
            $('#listaCena').empty();
            
Object.keys(response).forEach(function(tipo) {
    var listaID;
    if (tipo === '01') {
        listaID = '#listaDesayuno';
    } else if (tipo === '02') {
        listaID = '#listaAlmuerzo';
    } else if (tipo === '03') {
        listaID = '#listaCena';
    }

    response[tipo].forEach(function(plato) {
        if (plato.nombre !== null && plato.proteinas !== null && plato.lipidos !== null && plato.carbohidratos !== null && plato.calorias !== null) {
            var nombre = plato.plato;
            var nombre = plato.plato;
            var prot = plato.proteinas;
            var lip = plato.lipidos;
            var carb = plato.carbohidratos;
            var cal = plato.calorias;

            $(listaID).append('<tr>' +
                '<td style="text-align: left;font-size:13px;" class="columna-item">' + 
                '<i class="fa-regular fa-hand-point-right"></i>	'+ '	' + nombre + '</td>' +
                '<td style="text-align: right;font-size:13px;" class="columna-item">' + prot + '</td>' +
                '<td style="text-align: right;font-size:13px;" class="columna-item">' + lip + '</td>' +
                '<td style="text-align: right;font-size:13px;" class="columna-item">' + carb + '</td>' +
                '<td style="text-align: right;font-size:13px;" class="columna-item">' + cal + '</td>' +
                '</tr>');
        }
    });
});
            
            actualizarTotalesDesayuno();
            actualizarTotalesAlmuerzo();
            actualizarTotalesCena();
            $("#CtnListaAlimentos").show();
            $('#modalLoading').modal('hide');
            
        },
        error: function(xhr, status, error) {
            mostrarAlertaError(xhr, status, error);
        }
    });
}


function actualizarTotalesDesayuno() {
        var totalProteinas = 0;
        var totalLipidos = 0;
        var totalCarbohidratos = 0;
        var totalCalorias = 0;

        $("#listaDesayuno tr").each(function() {
            var proteinas = parseFloat($(this).find("td:nth-child(2)").text());
            var lipidos = parseFloat($(this).find("td:nth-child(3)").text());
            var carbohidratos = parseFloat($(this).find("td:nth-child(4)").text());
            var calorias = parseFloat($(this).find("td:nth-child(5)").text());

            if (!isNaN(proteinas)) totalProteinas += proteinas;
	        if (!isNaN(lipidos)) totalLipidos += lipidos;
	        if (!isNaN(carbohidratos)) totalCarbohidratos += carbohidratos;
	        if (!isNaN(calorias)) totalCalorias += calorias;
        });

        $("#tProtDesayuno").text(totalProteinas.toFixed(2));
        $("#tLipDesayuno").text(totalLipidos.toFixed(2));
        $("#tCarbDesayuno").text(totalCarbohidratos.toFixed(2));
        $("#tCalDesayuno").text(totalCalorias.toFixed(2));
    }


function actualizarTotalesAlmuerzo() {
    var totalProteinas = 0;
    var totalLipidos = 0;
    var totalCarbohidratos = 0;
    var totalCalorias = 0;

    $("#listaAlmuerzo tr").each(function() {
        var proteinas = parseFloat($(this).find("td:nth-child(2)").text());
        var lipidos = parseFloat($(this).find("td:nth-child(3)").text());
        var carbohidratos = parseFloat($(this).find("td:nth-child(4)").text());
        var calorias = parseFloat($(this).find("td:nth-child(5)").text());

        if (!isNaN(proteinas)) totalProteinas += proteinas;
        if (!isNaN(lipidos)) totalLipidos += lipidos;
        if (!isNaN(carbohidratos)) totalCarbohidratos += carbohidratos;
        if (!isNaN(calorias)) totalCalorias += calorias;
    });

    $("#tProtAlmuerzo").text(totalProteinas.toFixed(2));
    $("#tLipAlmuerzo").text(totalLipidos.toFixed(2));
    $("#tCarbAlmuerzo").text(totalCarbohidratos.toFixed(2));
    $("#tCalAlmuerzo").text(totalCalorias.toFixed(2));
}


function actualizarTotalesCena() {
    var totalProteinas = 0;
    var totalLipidos = 0;
    var totalCarbohidratos = 0;
    var totalCalorias = 0;

    $("#listaCena tr").each(function() {
        var proteinas = parseFloat($(this).find("td:nth-child(2)").text());
        var lipidos = parseFloat($(this).find("td:nth-child(3)").text());
        var carbohidratos = parseFloat($(this).find("td:nth-child(4)").text());
        var calorias = parseFloat($(this).find("td:nth-child(5)").text());

        if (!isNaN(proteinas)) totalProteinas += proteinas;
        if (!isNaN(lipidos)) totalLipidos += lipidos;
        if (!isNaN(carbohidratos)) totalCarbohidratos += carbohidratos;
        if (!isNaN(calorias)) totalCalorias += calorias;
    });

    $("#tProtCena").text(totalProteinas.toFixed(2));
    $("#tLipCena").text(totalLipidos.toFixed(2));
    $("#tCarbCena").text(totalCarbohidratos.toFixed(2));
    $("#tCalCena").text(totalCalorias.toFixed(2));
}
