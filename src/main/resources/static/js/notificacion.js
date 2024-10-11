function verAnexoDoc(nuAnn, nuEmi, nuAne){
    $.ajax({
        type:"Post",
        url : '/appnotificaciondedocumento/verAnexoDoc/',
        xhrFields: {
            responseType: 'blob'
        },
        data: {
        	nuAnn : nuAnn,
        	nuEmi : nuEmi,
        	nuAne : nuAne
        },	
        beforeSend: function(xhr) {
        	loadding(true);
        },
        error: function (xhr, error, code){
        	console.log("generarReporte, error...." + xhr.status);
        	mostrarNotificacion("Ocurrió un error al procesar. Detalle del error:"+ xhr.responseText+".", "danger");        	
        	loadding(false);
        },
        success: function (result, status, xhr) {
            if(result.size > 0){
                var filename = "xxx.pdf";
                var disposition = xhr.getResponseHeader('Content-Disposition');

                if (disposition) {
                    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches !== null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                }
                var linkelem = document.createElement('a');
                try {
                    var blob = new Blob([result], { type: 'application/octet-stream' });
                    
                    if (typeof window.navigator.msSaveBlob !== 'undefined') {
                        //   IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                        window.navigator.msSaveBlob(blob, filename);
                    } else {
                        var URL = window.URL || window.webkitURL;
                        var downloadUrl = URL.createObjectURL(blob);
                        if (filename) {
                            // use HTML5 a[download] attribute to specify filename
                            var a = document.createElement("a");

                            // safari doesn't support this yet
                            if (typeof a.download === 'undefined') {
                                window.location = downloadUrl;
                            } else {
                                a.href = downloadUrl;
                                a.download = filename;
                                document.body.appendChild(a);
                                a.target = "_blank";
                                a.click();

                                window.onfocus = function () {
                                    document.body.removeChild(a);
                                    window.URL.revokeObjectURL(downloadUrl);
                                }
                            }
                        } else {
                            window.location = downloadUrl;
                        }
                    }
                    loadding(false);
                } catch (ex) {
                    
                }
            }
            loadding(false);
        }
    });
}

function verAnexo(nuAne) {
	console.log("nuAne--> " + nuAne);
	var nuAnn 	= $('#nuAnn').val();   
	var nuEmi 	= $('#nuEmi').val();
	verAnexoDoc(nuAnn, nuEmi, nuAne);
}

$(document).ready(function() {
	
	var nuAnn 	= $('#nuAnn').val();   
	var nuEmi 	= $('#nuEmi').val();
	var nuDes 	= $('#nuDes').val();
	
    $.ajax({
        type:"Post",
        url : '/appnotificaciondedocumento/verDocumentoPrincipal/',
        xhrFields: {
            responseType: 'blob'
        },
        data: {
        	nuAnn : nuAnn,
        	nuEmi : nuEmi,
        	nuDes :	nuDes
        },	
        beforeSend: function(xhr) {
        	loadding(true);
        },
        error: function (xhr, error, code){
        	console.log("generarReporte, error...." + xhr.status);
        	mostrarNotificacion("Ocurrió un error al procesar. Detalle del error:"+ xhr.responseText+".", "danger");        	
        	loadding(false);
        },
        success: function (result, status, xhr) {
            if(result.size > 0){
                var filename = "xxx.pdf";
                var disposition = xhr.getResponseHeader('Content-Disposition');

                if (disposition) {
                    var filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
                    var matches = filenameRegex.exec(disposition);
                    if (matches !== null && matches[1]) filename = matches[1].replace(/['"]/g, '');
                }
                var linkelem = document.createElement('a');
                try {
                    var blob = new Blob([result], { type: 'application/pdf' });
                    
                    if (typeof window.navigator.msSaveBlob !== 'undefined') {
                        //   IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
                        window.navigator.msSaveBlob(blob, filename);
                    } else {
                        //var URL = window.URL || window.webkitURL;
                        var downloadUrl = URL.createObjectURL(blob);
                        if (filename) {
                            // use HTML5 a[download] attribute to specify filename
                            var a = document.createElement("a");

                            // safari doesn't support this yet
                            if (typeof a.download === 'undefined') {
                                window.location = downloadUrl;
                            } else {
                            	console.log('auiiiiiii');
//                                a.href = downloadUrl;
//                                a.download = filename;
//                                document.body.appendChild(a);
//                                a.target = "_blank";
//                                a.click();

                                var sectionVistaPrevia= '<div class="form-row rowPrincipal" style="border: 1px solid #d0d0d0;">' +
                                '<iframe id="blah" src="' + downloadUrl + '"></iframe>' +
                                	'</div>';
                                $('#loadVistaPrevia').append(sectionVistaPrevia);
    
                                window.onfocus = function () {
                                    //document.body.removeChild(a);
                                    window.URL.revokeObjectURL(downloadUrl);
                                }
                            }
                        } else {
                            window.location = downloadUrl;
                        }
                    }
                    loadding(false);
                } catch (ex) {
                    
                }
            }
            loadding(false);
        }
    });
} );
