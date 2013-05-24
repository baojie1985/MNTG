var markers = new Array();
var stepNumber = 0;
var offset = 0;
var maxOffset = 1;
var geocoder = null;

var map = null;
var rectangle = null;
var marker1 = null;
var marker2 = null;
var nullMarkers = 0;

var maxStepNumber = 20;
var offsetDelay = 1000;
var endDelay = 3000;
var slider = null;
var overlays = new Array();
var defaultlatlng = new google.maps.LatLng(44.965,-93.275);
var xmlHttp = null;

function get_rectangle_initial_bounds() {
	var center = defaultlatlng;

	var width = 0.786895;
	var height = 0.26429;
	var north = center.lat()+height/2;
	var east = center.lng()+width/2;
	var south = center.lat()-height/2;
	var west = center.lng()-width/2;
	
	var rectBounds = new google.maps.LatLngBounds(new google.maps.LatLng(south+0.25*height, west+0.25*width), new google.maps.LatLng(north-0.25*height,east-0.25*width));
	return	rectBounds;
}

function get_rectangle_bounds_old() {
	var mapBounds = map.getBounds();
	var north = mapBounds.getNorthEast().lat();
	var east = mapBounds.getNorthEast().lng();
	var south = mapBounds.getSouthWest().lat();
	var west = mapBounds.getSouthWest().lng();
	var width = east-west;
	var height = north-south;
	var rectBounds = new google.maps.LatLngBounds(new google.maps.LatLng(south+0.25*height, west+0.25*width), new google.maps.LatLng(north-0.25*height,east-0.25*width));
	return	rectBounds;
}

function get_rectangle_bounds() {
	var mapBounds = map.getBounds();
	var north = Math.max(marker1.getPosition().lat(), marker2.getPosition().lat());
	var east = Math.max(marker1.getPosition().lng(), marker2.getPosition().lng());
	var south = Math.min(marker1.getPosition().lat(), marker2.getPosition().lat());
	var west = Math.min(marker1.getPosition().lng(), marker2.getPosition().lng());
	var rectBounds = new google.maps.LatLngBounds(new google.maps.LatLng(south, west), new google.maps.LatLng(north,east));
	return	rectBounds;
}

function map_center_changed() {
	//rectangle.setBounds(get_rectangle_bounds());
}
function map_click(event){
	if(nullMarkers == 0){
		if(rectangle != null){
			rectangle.setMap(null);
			rectangle = null;
		}
		
		if(marker1 != null)
			marker1.setMap(null);
		if(marker2 != null)
			marker2.setMap(null);

		marker1 = new google.maps.Marker({
	          map: map,
       	   position: event.latLng
        	});
		nullMarkers = nullMarkers+1;
	} else if (nullMarkers == 1) {
		marker2 = new google.maps.Marker({
	          map: map,
       	   position: event.latLng
        	});
		nullMarkers = nullMarkers+1;
		loadRectanlge();
		nullMarkers = 0;
	}
}
function rectanlgeBoundsChanged(){
}
function loadRectanlge() {
	if(rectangle == null)
		rectangle = new google.maps.Rectangle();
	var rectOptions = {
	strokeColor: "#FF0000",
	strokeOpacity: 0.8,
	strokeWeight: 2,
	fillColor: "#FF0000",
	fillOpacity: 0.35,
	map: map,
	bounds: get_rectangle_bounds(),
//	editable: true
	};
	rectangle.setOptions(rectOptions);
}
function initializeMap() {
	var myOptions = {
		zoom: 11,
		center: defaultlatlng,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};
	map = new google.maps.Map(document.getElementById("map_canvas"),
			myOptions);
	geocoder = new google.maps.Geocoder();
	map.enableKeyDragZoom({
	  visualEnabled: true,
	  visualPosition: google.maps.ControlPosition.LEFT,
	  visualPositionOffset: new google.maps.Size(35, 0),
	  visualPositionIndex: null,
	  visualSprite: "http://maps.gstatic.com/mapfiles/ftr/controls/dragzoom_btn.png",
	  visualSize: new google.maps.Size(20, 20),
	  visualTips: {
	    off: "Enable Drag to Zoom",
	    on: "Disable Drag to Zoom"
	  }
        });

	//google.maps.event.addDomListener(window, 'load', loadRectanlge);
	//google.maps.event.addListener(map, 'center_changed', map_center_changed);
	//google.maps.event.addListener(map, 'zoom_changed', map_center_changed);
	google.maps.event.addListener(map, 'click', map_click);

	//loadRectanlge();
}

var annimationEnabled = false;

function toggleAnnimationEnabled() {
	annimationEnabled = !annimationEnabled;
}

function annimateTraffic() {
	if (overlays.length > 0 && annimationEnabled) {
		document.getElementById("step_number_display").innerHTML = stepNumber;
		slider.setValue(stepNumber);
		stepNumber++;
		
		if (stepNumber > maxStepNumber) {
			stepNumber = 0;
			setTimeout('annimateTraffic()', endDelay);
		} else {
			setTimeout('annimateTraffic()', offsetDelay);
		}
		
	} else {
		setTimeout('annimateTraffic()', offsetDelay);
	}
}

function createTimeoutHandler(step) {
	return function() { 
		markers[step].setPosition(new google.maps.LatLng(markers[step].getPosition().lat() - 1, markers[step].getPosition().lng() + 1));
	}
}

function getTrafficBrinkhoff() {
	var filter=/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	var email = document.getElementById('TrafficRequest.email').value;
	if(email == '') {
		alert('Please enter your email address. Your request will not be processed at that time!');
		return;
	}
	else if (!filter.test(email)){
		alert('Please enter a valid email address. Your request will not be processed at that time!');
		return;
	}

	var generationArea = rectangle == null? map:rectangle;
	new Ajax.Updater('response', '/traffic_requests/save_request', {
	parameters: {
		"data[TrafficRequest][upperlat]": generationArea.getBounds().getNorthEast().lat(),
		"data[TrafficRequest][upperlong]": generationArea.getBounds().getSouthWest().lng(),
		"data[TrafficRequest][lowerlat]": generationArea.getBounds().getSouthWest().lat(),
		"data[TrafficRequest][lowerlong]": generationArea.getBounds().getNorthEast().lng(),
		"data[TrafficRequest][zoom]": map.getZoom(),
		"data[TrafficRequest][name]": document.getElementById('TrafficRequest.name').value,
		"data[TrafficRequest][email]": document.getElementById('TrafficRequest.email').value,
		"data[TrafficRequest][objBegin]": document.getElementById('TrafficRequest.objBegin').value,
		"data[TrafficRequest][extObjBegin]": document.getElementById('TrafficRequest.extObjBegin').value,
		"data[TrafficRequest][objPerTime]": document.getElementById('TrafficRequest.objPerTime').value,
		"data[TrafficRequest][extObjPerTime]": document.getElementById('TrafficRequest.extObjPerTime').value,
		"data[TrafficRequest][numObjClasses]": document.getElementById('TrafficRequest.numObjClasses').value,
		"data[TrafficRequest][numExtObjClasses]": document.getElementById('TrafficRequest.numExtObjClasses').value,
		"data[TrafficRequest][maxTime]": document.getElementById('TrafficRequest.maxTime').value,
		"data[TrafficRequest][reportProb]": document.getElementById('TrafficRequest.reportProb').value,
		"data[TrafficRequest][msd]": document.getElementById('TrafficRequest.msd').value	
	},
	onSuccess: function(transport) {
		if (transport.responseText.indexOf("No counties") >= 0) {
			map.setCenter(defaultlatlng);
			alert('Please select a location inside the United States.');
		}
		setTimeout("clearResponse()", 3000);
	}
	});
}

function getTrafficBerlinMOD() {
	var filter=/^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	var email = document.getElementById('TrafficRequest.email').value;
	if(email == '') {
		alert('Please enter your email address. Your request will not be processed at that time!');
		return;
	}
	else if (!filter.test(email)){
		alert('Please enter a valid email address. Your request will not be processed at that time!');
		return;
	}
	if(document.getElementById('TrafficRequest.scaleFactor').value == ''){
		valid = false;
		alert('Please enter a valid email address. Your request will not be processed at that time!');
		return;
	}

	var generationArea = rectangle == null? map:rectangle;
        new Ajax.Updater('response', '/traffic_requests/save_request', {
        parameters: {
                "data[TrafficRequest][upperlat]": generationArea.getBounds().getNorthEast().lat(),
                "data[TrafficRequest][upperlong]": generationArea.getBounds().getSouthWest().lng(),
                "data[TrafficRequest][lowerlat]": generationArea.getBounds().getSouthWest().lat(),
                "data[TrafficRequest][lowerlong]": generationArea.getBounds().getNorthEast().lng(),
                "data[TrafficRequest][zoom]": map.getZoom(),
                "data[TrafficRequest][name]": document.getElementById('TrafficRequest.name').value,
                "data[TrafficRequest][email]": document.getElementById('TrafficRequest.email').value,
		"data[TrafficRequest][scaleFactor]": document.getElementById('TrafficRequest.scaleFactor').value
        },
        onSuccess: function(transport) {
                if (transport.responseText.indexOf("No counties") >= 0) {
                        map.setCenter(defaultlatlng);
                        alert('Please select a location inside the United States.');
                }
		setTimeout("clearResponse()", 3000);
        }
        });
}

function clearResponse() {
	document.getElementById('response').innerHTML = '';
}

var loadingTrafficStep = false;
function showTrafficStep(step, delayClear) {
	if (loadingTrafficStep) {
		return;
	}
	loadingTrafficStep = true;
	if (delayClear) {
		setTimeout('clearOverlays( ' + step + ')', 50);
	} else {
		clearOverlays(step);
	}
	overlays[step].setMap(map);
	document.getElementById("step_number_display").innerHTML = step;
	stepNumber = step;
	loadingTrafficStep = false;
}


function clearOverlays(step) {
	for (var i = overlays.length - 1; i >= 0; i--) {
		if (i != step) {
			overlays[i].setMap(null);
		}
	}
}

function getAddress() {
	var address = document.getElementById("searchValue").value;
	geocoder.geocode( { 'address': address}, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			map.fitBounds(results[0].geometry.viewport);
		} else {
			alert("Geocode was not successful for the following reason: " + status);
		}
	});
}



function toggleHide(element){
   var temp = document.getElementById(element);
   if (temp.style.display == "none") {
      temp.style.display = "";
   } else {
      temp.style.display = "none";
   }
}

function showElement(element){
   var temp = document.getElementById(element);
   temp.style.display = "";
}

function hideElement(element){
   var temp = document.getElementById(element);
   temp.style.display = "none";
}

function createOverlays() {
	for (var i = 0; i < steps.length; i++) {
		overlays[i] = new MarkersOverlay({}, steps[i], cars_ids[i]);
	}	
	overlays[0].setMap(map);
}
function submitFeedback() {
	var msg = document.getElementById('feedback.message').value;
	var name = document.getElementById('feedback.name').value;
	var email = document.getElementById('feedback.email').value;
	var url="http://mntg.cs.umn.edu/submitfeedback.php?name="+name+"&email="+email+"&msg="+msg;
	sendHTTPRequest(url,submitFeedbackResponseHandler, "GET");
}
function submitFeedbackResponseHandler(){
	if ( xmlHttp.readyState == 4 && xmlHttp.status == 200 ) 
	{
		//alert(xmlHttp.responseText);
		
	}
}

function sendHTTPRequest(url,responseHandler, method) {
		xmlHttp = new XMLHttpRequest(); 
		//xmlHttp.setRequestHeader('Origin', 'dmlab.cs.umn.edu');
		xmlHttp.onreadystatechange = responseHandler;
		xmlHttp.open( method, url, true );
		xmlHttp.send( null );
}