var d       = new Date();
var n       = d.getFullYear();
var limInf  = 2000;

var mousePoint = {
		clientX:0,
		clientY:0
	};

var HttpStatus = {
		/* SUCCESS*/
		OK  				: 200,
		Accepted  			: 202,
		/* CLIENT ERRORS */
	    BadRequest			: 400,
	    Forbidden			: 403,
	    NotFound 			: 404,
	    /* SERVER ERRORS */
	    InternalServerError	: 500
	};


function is_chrome(){
	return navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
}

function movimientomouse(event){
	var evento = (window.event) ? window.event : event;
	mousePoint.clientX = evento.clientX;
	mousePoint.clientY = evento.clientY
}

function mousefuera(){
	mousePoint.clientX = 0;
	mousePoint.clientY = 0;
}

function soloEnteros(e){
    var key = e.charCode;
    //console.log(key);
    return key >= 48 && key <= 57;
}

function soloLetras(e){
    var key = e.charCode;
    console.log(key);
    return ((key >= 65 && key <= 90) || (key == 209)) || ((key >= 97 && key <= 122) || (key == 241)) || (key == 32) || 
           (key == 225 || key == 193) || (key == 233 || key == 201) || (key == 237 || key == 205) || (key == 243 || key == 211) || 
           (key == 250 || key == 218) || (key == 252 || key == 220);
}

function soloLetrasYNumeros(e){
    var key = e.charCode;
    console.log(key);
    return ((key >= 65 && key <= 90) || (key == 209)) || ((key >= 97 && key <= 122) || (key == 241)) || (key >= 48 && key <= 57);
}

function soloLetrasNumeroOtros(e){
    var key = e.charCode;
    //console.log(key);
    return (key >= 65 && key <= 90) || (key >= 97 && key <= 122) || (key == 32) || (key == 45) || (key == 47) || (key == 176) ||
           (key == 225 || key == 193) || (key == 233 || key == 201) || (key == 237 || key == 205) || (key == 243 || key == 211) || 
           (key == 250 || key == 218 || key == 220) || (key >= 48 && key <= 57);
}

function soloLetrasNumeroOtrosAsunto(e){
    var key = e.charCode;
    console.log(key);
    return ((key >= 65 && key <= 90) || (key == 209)) || ((key >= 97 && key <= 122) || (key == 241))  || (key == 32) || (key == 45) || (key == 47) || (key == 176) || (key == 130) || (key == 132) ||
    	   (key == 91) || (key == 93) || (key == 95) || (key == 58) || (key == 59) || (key == 44) || (key == 46) || (key == 39) ||
           (key == 225 || key == 193) || (key == 233 || key == 201) || (key == 237 || key == 205) || (key == 243 || key == 211) || 
           (key == 250 || key == 218 || key == 220) || (key >= 48 && key <= 57);
}

function soloDecimales(evt,input){
    // Backspace = 8, Enter = 13, ‘0′ = 48, ‘9′ = 57, ‘.’ = 46, ‘-’ = 43
    var key = window.Event ? evt.which : evt.keyCode;    
    var chark = String.fromCharCode(key);
    var tempValue = input.value+chark;
    if(key >= 48 && key <= 57){
        if(filter(tempValue)=== false){
            return false;
        }else{       
            return true;
        }
    }else{
          if(key == 8 || key == 13 || key == 0) {     
              return true;              
          }else if(key == 46){
                if(filter(tempValue)=== false){
                    return false;
                }else{       
                    return true;
                }
          }else{
              return false;
          }
    }
}

function filter(__val__){
    var preg = /^([0-9]+\.?[0-9]{0,2})$/; 
    if(preg.test(__val__) === true){
        return true;
    }else{
       return false;
    }
    
}

function loadding(onOf) {
    if (onOf) {
        var div="<div id='loadding' class='box'><div class='image'><img align='absmiddle' src='/appnotificaciondedocumento/images/loading.gif'></div><div class='line1'>PROCESANDO</div><div class='line2'>Ejecutando petición, por favor espere...</div></div>";
        jQuery.blockUI({
            message: div,
            css: {
                border: 'none',
                padding: '0px',
                backgroundColor: ''
            },
            overlayCSS: {
                backgroundColor: 'black',
                opacity: 0.10
            }
        });        
    }
    else {
        jQuery.unblockUI();
    }
}

function mostrarModal(id){
    $(id).modal("show");
}

function ocultarModal(id){
    $(id).modal('hide');
}

function habilitarBoton(id){
	$(id).removeClass("btn btn-outline-secondary my-1").addClass("btn btn-outline-primary my-1");
    $(id).removeAttr("disabled");
}

function deshabilitarBoton(id){
	$(id).removeClass("btn btn-outline-primary my-1").addClass("btn btn-outline-secondary my-1");
    $(id).attr("disabled", true);
}

function ocultarControl(id){
	$(id).css("display","none");
}

function mostrarControl(id){
	$(id).css("display","");
}

function habilitarControl(id){
	$(id).prop('disabled', false);
}

function deshabilitarControl(id){
	$(id).prop('disabled', true);
}

function habilitarControlSoloLectura(id){
	$(id).prop('readonly', true);
}

function deshabilitarControlSoloLectura(id){
	$(id).prop('readonly', false);
}

function controlRequerido(id){
	$(id).prop('required', true);
}

function controlNoRequerido(id){
	$(id).prop('required', false);
}

function checkControl(id){
	$(id).prop('checked', true);
}

function uncheckControl(id){
	$(id).prop('checked', false);
}

function mostrarNotificacion(mensaje, tipo){
	$.notify({
    	// options
    	icon: 'fas fa-info-circle',
    	title: '<strong>Mensaje: </strong>',
    	message: mensaje
    },{
    	type: tipo,
    	allow_dismiss: true,
    	newest_on_top: false,
    	placement: {
    		from: "top",
    		align: "right"
    	},
    	offset: 20,
    	spacing: 10,
    	z_index: 1031,
    	delay: 5000,
    	timer: 1000,
    	mouse_over: "pause",
    	animate: {
    		enter: 'animated bounceInDown',
    		exit: 'animated bounceOutUp'
    	}
    });
}


if(is_chrome()){
	document.onmouseout		=	mousefuera;
	document.onmousemove	=	movimientomouse;
}
