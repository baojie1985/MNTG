	function draw2ValRegionsMap(div_id, count_title1, count_title2, rows) {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Country');
        data.addColumn('number', count_title1);
		data.addColumn('number', count_title2);
        data.addRows(rows);
        var options = {};
        var chart = new google.visualization.GeoChart(document.getElementById(div_id));
        chart.draw(data, options);
   }
function draw1ValRegionsMap(div_id, count_title1, rows) {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Country');
        data.addColumn('number', count_title1);
        data.addRows(rows);
        var options = {};
        var chart = new google.visualization.GeoChart(document.getElementById(div_id));
        chart.draw(data, options);
   }
  
  function draw2ColChart(div_id, chart_title, yaxis_title, col1val, col2val) {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Visits');
	data.addColumn('number', 'Total');
	data.addColumn('number', 'Unique');
	data.addRows([
	  ['', col1val, col2val],
	]);

	var options = {
	  title: chart_title,
	  vAxis: {title: yaxis_title, chxs:'0N*f0*,'}
	};

	var chart = new google.visualization.ColumnChart(document.getElementById(div_id));
	chart.draw(data, options);
  }
  function draw1ColChart(div_id, chart_title, yaxis_title, col1val) {
	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Visits');
	data.addColumn('number', 'Total');
	data.addRows([
	  ['', col1val],
	]);

	var options = {
	  title: chart_title,
	  vAxis: {title: yaxis_title, chxs:'0N*f0*,'}
	};

	var chart = new google.visualization.ColumnChart(document.getElementById(div_id));
	chart.draw(data, options);
  }