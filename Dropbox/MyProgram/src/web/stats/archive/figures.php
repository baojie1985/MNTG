<?php
$accounts = array();
array_push($accounts, array("mokbel","mokbel"));
array_push($accounts, array("dmlab","dmlab"));

$auth = false;
if(isset($_POST["username"])&&isset($_POST["passcode"])) {
	for($i=0; $i < count($accounts); ++$i) {
		if($_POST["username"] == $accounts[$i][0]){
			if($_POST["passcode"] == $accounts[$i][1]){
				$auth = true;
			}
		}
	}
}
if(!$auth)
{
	echo "Authentication Failed<br/>";
	exit(1);
}
?>
<?php
include 'stats.php';
$gen_total = get_count("stats_traffic_gen_page", false);
$gen_unique = get_count("stats_traffic_gen_page", true);
$gen_total_by_country = get_count_by_country("stats_traffic_gen_page", false);
$gen_unique_by_country = get_count_by_country("stats_traffic_gen_page", true);

$req_total = get_count("stats_traffic_requests", false);
$req_total_by_country = get_count_by_country("stats_traffic_requests", false);

$download_total = get_count("stats_traffic_downloads", false);
$download_unique = get_count("stats_traffic_downloads", true);
$download_total_by_country = get_count_by_country("stats_traffic_downloads", false);
$download_unique_by_country = get_count_by_country("stats_traffic_downloads", true);

$view_total = get_count("stats_traffic_view_page", false);
$view_unique = get_count("stats_traffic_view_page", true);
$view_total_by_country = get_count_by_country("stats_traffic_view_page", false);
$view_unique_by_country = get_count_by_country("stats_traffic_view_page", true);


?>
<html>

<header>
<script type="text/javascript" src="stats.js"></script>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script type="text/javascript">
  google.load("visualization", "1", {packages:["corechart"]});
  google.load('visualization', '1', {'packages': ['geochart']});
</script>
</header>

<body>
<table>
<tr>
<td>
<div style="width: 1350px; height: 50px;text-align:center;">
<h1>Traffic Generation Page</h1>
</div>
</td>
</tr>
<tr>
<td>
<div style="width: 1350px; height: 300px;">
    <div id="gen_chart_div1" style="width: 450px; height: 400px;float:left;"></div>
	<div id="gen_chart_div2" style="width: 450px; height: 400px;float:left;"></div>
	<div id="gen_chart_div3" style="width: 450px; height: 400px;float:left;"><h2>Place holder for time-aggregated statistics</h2></div>
</div>
<script>draw2ColChart('gen_chart_div1', 'Trrafic Generation Page Visits', 'Number of Visits', <?php echo $gen_total ?>, <?php echo $gen_unique ?>)</script>
<script>draw2ValRegionsMap('gen_chart_div2', 'Total Visits', 'Unique Visits', <?php echo"[";
for($i = 0; $i < count($gen_total_by_country); ++$i) {
echo "['".$gen_total_by_country[$i]["country_name"]."',".$gen_total_by_country[$i]["count"].",".$gen_unique_by_country[$i]["count"]."]";
if($i < count($gen_total_by_country)-1)
	echo ",";
}
echo "]";
?>)</script>
</td>
</tr>
<tr>
<td>
<div style="width: 1350px; height: 50px;text-align:center;">
<h1>Traffic Requests</h1>
</div>
</td>
</tr>
<tr>
<td>
<div style="width: 1350px; height: 300px;">
    <div id="req_chart_div1" style="width: 450px; height: 400px;float:left;"></div>
	<div id="req_chart_div2" style="width: 450px; height: 400px;float:left;"></div>
	<div id="req_chart_div3" style="width: 450px; height: 400px;float:left;"><h2>Place holder for time-aggregated statistics</h2></div>
</div>
<script>draw1ColChart('req_chart_div1', 'Trrafic Requests', 'Number of Requests', <?php echo $req_total ?>)</script>
<script>draw1ValRegionsMap('req_chart_div2', 'Requests', <?php echo"[";
for($i = 0; $i < count($req_total_by_country); ++$i) {
echo "['".$req_total_by_country[$i]["country_name"]."',".$req_total_by_country[$i]["count"]."]";
if($i < count($req_total_by_country)-1)
	echo ",";
}
echo "]";
?>)</script>
</td>
</tr>

<tr>
<td>
<div style="width: 1350px; height: 50px;text-align:center;">
<h1>Traffic Downloads</h1>
</div>
</td>
</tr>
<tr>
<td>
<div style="width: 1350px; height: 300px;">
    <div id="downloads_chart_div1" style="width: 450px; height: 400px;float:left;"></div>
	<div id="downloads_chart_div2" style="width: 450px; height: 400px;float:left;"></div>
	<div id="downloads_chart_div3" style="width: 450px; height: 400px;float:left;"><h2>Place holder for time-aggregated statistics</h2></div>
</div>
<script>draw2ColChart('downloads_chart_div1', 'Traffic Downloads', 'Number of Downloads', <?php echo $download_total ?>, <?php echo $download_unique ?>)</script>
<script>draw2ValRegionsMap('downloads_chart_div2', 'Total Visits', 'Unique Visits', <?php echo"[";
for($i = 0; $i < count($download_total_by_country); ++$i) {
echo "['".$download_total_by_country[$i]["country_name"]."',".$download_total_by_country[$i]["count"].",".$download_unique_by_country[$i]["count"]."]";
if($i < count($download_total_by_country)-1)
	echo ",";
}
echo "]";
?>)</script>
</td>
</tr>

<tr>
<td>
<div style="width: 1350px; height: 50px;text-align:center;">
<h1>Traffic Visualization Page</h1>
</div>
</td>
</tr>

<tr>
<td>
<div style="width: 1350px; height: 300px;">
    <div id="view_chart_div1" style="width: 450px; height: 400px;float:left;"></div>
	<div id="view_chart_div2" style="width: 450px; height: 400px;float:left;"></div>
	<div id="view_chart_div3" style="width: 450px; height: 400px;float:left;border-width:1px;"><h2>Place holder for time-aggregated statistics</h2></div>
</div>	
<script>draw2ColChart('view_chart_div1', 'Traffic Visualization Page Visits', 'Number of Visits', <?php echo $view_total ?>, <?php echo $view_unique ?>)</script>
<script>draw2ValRegionsMap('view_chart_div2', 'Total Visits', 'Unique Visits', <?php echo"[";
for($i = 0; $i < count($view_total_by_country); ++$i) {
echo "['".$view_total_by_country[$i]["country_name"]."',".$view_total_by_country[$i]["count"].",".$view_unique_by_country[$i]["count"]."]";
if($i < count($view_total_by_country)-1)
	echo ",";
}
echo "]";
?>)</script>
</td>
</tr>
</table>
</body>
</html>