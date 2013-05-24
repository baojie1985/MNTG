function MoveableMarker() {

    var id = null;
    var steps = new Array();
    var marker = null;
    
    this.getId = getId;
    this.setId = setId;
    this.setSteps = setSteps;
    this.setMarker = setMarker;
    this.getMarker = getMarker;
    this.advanceToStep = advanceToStep;

    function getId() {
        return id;
    }

    function setId(id) {
        this.id = id;
    }

    function setSteps(steps) {
	this.steps = steps;
    }

    function setMarker(marker) {
        this.marker = marker;

    }
    
    function getMarker() {
         return this.marker;
    }
    
    function advanceToStep(step) {
    	if (this.steps[step] != undefined) {
			if (!this.marker.getVisible()) {
				this.marker.setVisible(true);
			}
    		this.marker.setPosition(this.steps[step]);
    	} else {
			if (this.marker.getVisible()) {
				this.marker.setVisible(false);
			}
    	}
    }
}