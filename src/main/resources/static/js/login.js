function login(){
	if(validarFormLogin()){
		showLoader();
		var formulario = document.getElementById("formLogin");
    	formulario.submit();
	}
}

function validarFormLogin(){
	if($('#usuario').val()==''){
		toastr.info("El usuario es obligatorio", 'Info');
		return false;
	}
	if($('#clave').val()==''){
		toastr.info("La contraseña es obligatoria", 'Info');
		return false;
	}
	return true;
}

function configuraciones(){
	document.getElementById('loaderIngresar').style.display = 'none';
	var selectAniosEjecucion = document.getElementById("anioEjecucion");

	if (selectAniosEjecucion.options.length == 0) {
		toastr.info('No se encontraron años de ejecución', 'INFO');
	}
}

function showLoader(){
	document.getElementById('loaderIngresar').style.display = 'flex';
	document.getElementById('textIngresar').style.display = 'none';
	document.getElementById("btnIngresar").style.backgroundColor = '#fa9e9e';
	document.getElementById("btnIngresar").style.cursor = 'initial';
	$("#btnIngresar").prop("disabled", true);
}

function detectarEnter(event) {
    if (event.keyCode === 13) {
        login();
    }
}