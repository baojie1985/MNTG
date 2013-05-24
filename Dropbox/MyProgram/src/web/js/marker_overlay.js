function MarkersOverlay( options, points, ids )
{
   this.points = points;
   this.ids = ids;
   this.setValues( options );
   this.markerLayer = $j('<div />').addClass('overlay');
};

// MyOverlay is derived from google.maps.OverlayView
MarkersOverlay.prototype = new google.maps.OverlayView;

MarkersOverlay.prototype.onAdd = function()
{
   var $pane = $j(this.getPanes().overlayImage); // Pane 4
   $pane.append( this.markerLayer );
};

MarkersOverlay.prototype.onRemove = function()
{
   this.markerLayer.remove();
};

MarkersOverlay.prototype.draw = function()
{
   var projection = this.getProjection();
   var zoom = this.getMap().getZoom();
   var fragment = document.createDocumentFragment();

   this.markerLayer.empty(); // Empty any previous rendered markers

   var num_colors = 5;
   for(var i = 0; i < this.points.length; i++){
      var objectLocation = projection.fromLatLngToDivPixel( this.points[i] );
	//alert(this.ids[i]);
      var color_id = this.ids[i]%num_colors;
      var $point = $j('<div '
	    +'class="map-point" '
	    +'id="p'+i+'"'
	    +'title="'+i+'" '
	    +'style="'
	    +'width:8px; '
	    +'height:8px; '
	    +'left:' + (objectLocation.x - 15) + 'px; '
	    +'top:' + (objectLocation.y - 6) + 'px; '
	    +'position:absolute; '
	    +'">'
	    +'<img '
	    +'src="/img/car'+color_id+'.png" ' +
	    +'style="position: absolute;" '
	    +'/>' +
	    +'</div>');
      // Append the HTML to the fragment in memory
      fragment.appendChild( $point.get(0) );
   }

   // Now append the entire fragment from memory onto the DOM
   this.markerLayer.append(fragment);
};

