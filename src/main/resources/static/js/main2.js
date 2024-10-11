/* ---------------- CONGIFURACIONES DEL WSTRAMITEDOC ----------------------- */

var _wsocket=null;
var wSocketServer = 'ws://localhost:8083/wstradoc/chat/';
var urlBase = document.location.protocol+'//'+document.location.host+'/';
var rutaPri = '';
var wsRemite = '/BROWSER';
var wsDestino = 'APPCLIENT';
var appOnpeResponse=null;
var nrOperacion=null;

var accionOnDesktopTramiteDoc = {
    ejecutaFirma: 1,
    abrirDocumento: 2,
    abrirDocumentoPC: 3,
    cargarDocumento: 4,
    generaDocumento: 5,
    verificaSiExisteDoc: 6,
    getDirectorio: 7,
    abrirRutaDocs: 8,
    verificarRutaDirectorio: 9
};

var tipoEjecucionAppDesktop = {
    value: 0,
    noEjecutarAppDesktop: 0,
    ejeAppDesktopModoNormal: 1
};

function fn_firmarDocDesktop(urlDoc, rutaDoc, tipoFirma) {
    var param = "{\"urlDoc\": "+JSON.stringify(urlDoc)+", \"rutaDoc\": "+JSON.stringify(rutaDoc)+", \"tipoFirma\": "+JSON.stringify(tipoFirma)+"}";
    try {
        console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"EJECUTAR_FIRMA");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }    
}

function fn_abrirDocDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": "+JSON.stringify(purlDoc)+", \"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        sendMessageWS(JSON.stringify(param),"VER_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
} 

function fn_cargarDocumentoDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": "+JSON.stringify(purlDoc)+", \"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        sendMessageWS(JSON.stringify(param),"CARGAR_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en cargar el documento");
    }
} 

function fn_verificaSiExisteDocDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": "+JSON.stringify(prutaDoc)+",\"verBloqueo\": false}";
    try {
        sendMessageWS(JSON.stringify(param),"VERIFICAR_EXISTE_DOC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
} 
function fn_abrirDocFromPCDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        sendMessageWS(JSON.stringify(param),"ABRIR_DOCUMENTO_PC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
}

function fn_abrirRutaDocsDesktop(){
    try {
        sendMessageWS(null,"VER_RUTA_PRINCIPAL");
    } catch (ex) {
        alert("Fallo en ruta.");
    }
}

function fn_selecDirectorioDesktop(){
    try {
        sendMessageWS(null,"SELECCIONAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en Seleccionar directorio.");
    }
}

function fn_generaDocumentoDesktop(urlDoc, rutaDoc, remplazaArchivo) {
    try {
        if (!!!remplazaArchivo) {
            remplazaArchivo = false;
        }
        var param = "{\"urlDoc\": "+JSON.stringify(urlDoc)+", \"rutaDoc\": "+JSON.stringify(rutaDoc)+", \"remplazaArchivo\": "+remplazaArchivo+"}";   
        sendMessageWS(JSON.stringify(param),"GENERAR_DOCUMENTO");        
    } catch (ex) {
        alert("Fallo en generar el documento");
    }
}

function fn_verificarRutaDirectorio(prutaDir) {
    var param = "{\"rutaDir\": "+JSON.stringify(prutaDir)+"}";
    try {
        sendMessageWS(JSON.stringify(param), "VERIFICAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en verificar ruta Directorio.");
    }
} 

function onMessageReceivedWS(evt, callback) {
    var msg = JSON.parse(evt.data); // native API
    processMessageRecived(msg, callback);
}

function sendMessageWS_CONTINUE_APP(msg, accion){
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"'+accion+'" ,"nrOperacion":"'+nrOperacion+'"}';    
    console.log(msg);
    _wsocket.send(msg);
}

function cerrarConexion_wsocket(){
    sendMessageWS(null,"TERMINATE_APP");
    _wsocket.close();
}

function processMessageRecived(msg, callback){
   switch (msg.accion) 
        {
            case "TERMINATE_APP":
                var parm = "{\"retval\": \"OK\"}";
                sendMessageWS_CONTINUE_APP(JSON.stringify(parm),"CONTINUE_APP");
                break;
            case "CONEXION":
                var datosPC = JSON.parse(msg.message);
                if(!!datosPC && msg.nrOperacion===nrOperacion.toString()){
                    callback(datosPC);
                }                
                break;
            case "SELECCIONAR_DIRECTORIO":
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;
            case "VER_DOCUMENTO":  
                var auxMsg = msg;
                if(!!auxMsg && msg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="1"){
                        alert(auxMsg.message);
                    }
                }
                break;        
            case "ABRIR_DOCUMENTO_PC":  
                var auxMsg = msg;
                if(!!auxMsg && msg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert_Danger(" ", auxMsg.message);
                    }
                }
                break;                
            case "CARGAR_DOCUMENTO":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    /*if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert_Danger("Repositorio: ", auxMsg.message);
                        //alert(auxMsg.message);
                    }*/
                    callback(auxMsg);
                }
                break;    
            case "GENERAR_DOCUMENTO":
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;                
            case "VERIFICAR_EXISTE_DOC":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;             
            case "EJECUTAR_FIRMA":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    /*if(auxMsg.error==="1"){
                        alert(auxMsg.message);
                    }*/
                    callback(auxMsg);
                }
                break;                
            case "VERIFICAR_DIRECTORIO":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    callback(auxMsg);
                }
                break;                
            default:
                alert(msg);
                break;
        }   
}

function fn_runOnDesktop(pAccionOnDesktop, param, callback){
        switch (pAccionOnDesktop)
        {
            case 1:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };
                fn_firmarDocDesktop(param.urlDoc, param.rutaDoc, param.tipoFirma);
                break;
            case 2:
                fn_abrirDocDesktop(param.urlDoc, param.rutaDoc);
                callback(false);
                break;
            case 3:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_abrirDocFromPCDesktop(param.rutaDoc);
                break;
            case 4:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_cargarDocumentoDesktop(param.urlDoc, param.rutaDoc);
                break;
            case 5:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_generaDocumentoDesktop(param.urlDoc, param.rutaDoc,param.remplazaArchivo);
                break;
            case 6:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                
                fn_verificaSiExisteDocDesktop(param.rutaDoc);
                break;
            case 7:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                  
                fn_selecDirectorioDesktop();
                break;
            case 8:
                fn_abrirRutaDocsDesktop();
                break;
            case 9:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                
                fn_verificarRutaDirectorio(param.rutaDir);
                break;
            default:
        }    
}

function wsVerificarConexion(){
    sendMessageWS(null,"CONEXION");
}

function sendMessageWS(msg, accion){
    nrOperacion = Math.round(Math.random() * 0x1000000);    
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"'+accion+'" ,"nrOperacion":"'+nrOperacion+'"}';    
    console.log(msg);
    _wsocket.send(msg);
}

function connectToChatDesktop(callback){
    _wsocket = new WebSocket(wSocketServer + $.cookie('idChannel') +wsRemite);
    _wsocket.onmessage = onMessageReceivedWS;
}

function runOnDesktop(pAccionOnDesktop, param, callback) {
    if (_wsocket.readyState!==WebSocket.OPEN) {
        connectWS(function(){
            fn_runOnDesktop(pAccionOnDesktop, param, function(data){
                callback(data);
            }); 
        });
    }else{
        fn_runOnDesktop(pAccionOnDesktop, param, function(data){
            callback(data);
        });
    }
}

function finishConnect(data, callback){
	appOnpeResponse='OK';
	console.log(data);
    callback();
}

function connectWS(callback){
    var openTD = false;
    var connectWSFunction = function() {
        if(!openTD){
            openTD = true;
            try{
                cerrarConexion_wsocket();
            }catch(ex){}
            try{
                var url = 'Tramitedoc:accion=TraDoc?ws='+wSocketServer + $.cookie('idChannel') + '/?urlBase='+urlBase+'?rutaPri='+rutaPri;
                var desktopObj = document.getElementById('idTradocDesktop');
                desktopObj.href = url;
                desktopObj.click();                    
                desktopObj.href = "javascript:void(0);";
            }catch(ex){console.log(ex)}  
        }else{
            clearInterval(timereconnectWS);
			connectToChatDesktop();
			fn_cargarAppOnpePrincipal2(callback);
            
        }
        
    };
    var timereconnectWS = setInterval(connectWSFunction, 500); 
}



function fn_cargarAppOnpePrincipal2(callback){
    appOnpeConexion(function(data) {
            finishConnect(data, callback);
    });
}

function appOnpeConexion(callback){
    _wsocket.onmessage = function (evt){
        onMessageReceivedWS(evt, callback);
    };
    appOnpeResponse = "";
    var auxTime = 0;
    var waitToResponseApp = function() {
        auxTime = auxTime + 250;
        var clearTime=auxTime<=1000*30/*(30 segundos)*/;
        if (!!!appOnpeResponse&&clearTime) {
            if(_wsocket.readyState===_wsocket.OPEN){
                wsVerificarConexion();                
            }
            console.log("esperando");
            //console.log(auxTime);
            //Aqui se puede poner un loading.
        } else {
            clearInterval(time);
            if (!!!appOnpeResponse){
                try{_wsocket!==null?_wsocket.close():'';}catch(ex){}
            }
            //aqui se puede terminar el loading.
        }
    };
    var time = setInterval(waitToResponseApp, 250);         
}




/* ---------------- FUNCIONES UTILITARIAS ----------------------- */		  

function ajaxCall(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = '' + "/" + url;
//    if (!silent) {
//        loadding(true);
//    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function(request, status, error) {
//            alert(request.responseText);
//            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    console.log(objAjax);
    jQuery.ajax(objAjax);

}



/* ---------------- FUNCIONES INICIO ----------------------- */

function mergepdf(){
	ajaxCall("merge", null, function(data) {
		if(!!data && data.retval){
			jQuery('#filename').val(data.filename);
			jQuery('#url').val(data.url);
			console.log(data.filename);
			console.log(data.url);
			location.reload();
		}
	}, 'json', true, true, 'POST');  
}


function verPdf(nombreplanilla,year) {
	 // Obtener la URL del controlador REST
	 //const url = "download/viewfile/normal/"+nombreplanilla;
	const url = "download/viewfile/" + year + "/" + nombreplanilla;

													  
	// Abrir el archivo PDF en una nueva ventana
	 window.open(url, "_blank");
						
}

function verPdfMerged(nombreplanilla,year) {
	 // Obtener la URL del controlador REST
	 const url = "download/viewfile/merged/" + year + "/" + nombreplanilla;
													  
	// Abrir el archivo PDF en una nueva ventana
	 window.open(url, "_blank");
						
}

function uploadDocumentSigned(){
	var vnoPrefijo = '[AF]';
    var vnoDoc = jQuery('#filename').val();
    vnoDoc = vnoDoc.substring(0, vnoDoc.length - 4) + vnoPrefijo+".pdf";
    
    var p = new Array();
    p[0] = "noDoc=" + vnoDoc;        
                    
    ajaxCall("uploadpdf", p.join("&"), function(data) {
		console.log(data);
		if(!!data && data.retval){
			jQuery('#filename').val(data.filename);
			jQuery('#url').val(data.url);
		}
			
	}, 'json', true, true, 'POST');  
}

function signDocument(){
	
	connectWS(function(){
        signDocumentFunction(function(){
			try{
                cerrarConexion_wsocket();
                console.log('Se ha cerrado la conexion del tramite doc');
            }catch(ex){}
		});
    });
	
}

function signDocumentFunction(callback){
	var noDoc = 'TEMP|' + jQuery('#filename').val();
	var noUrl = jQuery('#url').val();
	var param={urlDoc:noUrl,rutaDoc:noDoc,tipoFirma:"3"};
	console.log(param);
	runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma, param, function(data){
		console.log(data);
		callback();
    });
}


$(function() {
	var tablePlanillas = new DataTable('#tablePlanillas', {
		scrollY: '400px',
//        scrollCollapse: true,
        "columns": [
		    { "width": "80%" },
		    { "width": "20%" }
	  	],
		language: {
            url: urlBase + 'language/Spanish.json',
        }
	});
	
});






