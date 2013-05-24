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

date_default_timezone_set('America/Chicago');
$downloads_folder = "downloads/";
$timestamp = date('l jS \of F Y h-i-s A');

$gen_total = get_count("stats_traffic_gen_page", false);
$gen_unique = get_count("stats_traffic_gen_page", true);
$gen_total_by_country = get_count_by_country("stats_traffic_gen_page", false);
$gen_unique_by_country = get_count_by_country("stats_traffic_gen_page", true);

$gen_stats_file = $downloads_folder."generation-page-visits-".$timestamp.".csv";
get_stats("stats_traffic_gen_page", $gen_stats_file);


$req_total = get_count("stats_traffic_requests", false);
$req_total_by_country = get_count_by_country("stats_traffic_requests", false);
$requests_stats_file = $downloads_folder."requests-".$timestamp.".csv";
$requests_str = get_stats("stats_traffic_requests", $requests_stats_file);
$requests_stats_combo_file = $downloads_folder."requests-".$timestamp."-combo.csv";
$combo_requests_array = get_combo_requests_stats($requests_stats_combo_file);

$download_total = get_count("stats_traffic_downloads", false);
$download_unique = get_count("stats_traffic_downloads", true);
$download_total_by_country = get_count_by_country("stats_traffic_downloads", false);
$download_unique_by_country = get_count_by_country("stats_traffic_downloads", true);

$download_stats_file = $downloads_folder."downloads-".$timestamp.".csv";
$download_str = get_stats("stats_traffic_downloads", $download_stats_file);


$view_total = get_count("stats_traffic_view_page", false);
$view_unique = get_count("stats_traffic_view_page", true);
$view_total_by_country = get_count_by_country("stats_traffic_view_page", false);
$view_unique_by_country = get_count_by_country("stats_traffic_view_page", true);

$view_stats_file = $downloads_folder."visualization-page-visits-".$timestamp.".csv";
get_stats("stats_traffic_view_page", $view_stats_file);

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
<h1>Traffic Requests (<a href="<?php echo $requests_stats_combo_file ?>">Download Stats File</a>)</h1>
</div>
</td>
</tr>
<tr>
<td>
<?php echo "<table border=\"2\">"; 
echo "<tr align=\"center\">";
echo "<td>request_id</td><td>email</td><td>hostname</td><td>request_timestamp</td><td>download_count</td><td>response_time<br/>(minutes)</td><td>city</td><td>region_name</td><td>country_name</td><td>generation_area</td><td>Scale_Factor</td><td>Beginning_Moving_Objects</td><td>Beginning_External_Objects</td><td>Moving_Objects_Per_Time_Unit</td><td>External_Objects_Per_Time_Unit</td><td>Classes_Count_of_Moving_Objects</td><td>Classes_Count_of_External_Objects</td><td>Time_Units</td><td>Report_Probability</td><td>Max_Speed_Div</td>";
echo "</tr>";
foreach ($combo_requests_array as $req_row) {
	echo "<tr align=\"center\">";
	echo "<td>".$req_row["request_id"]."</td><td>".$req_row["email"]."</td><td>".$req_row["hostname"]."</td><td>".$req_row["request_timestamp"]."</td><td>".$req_row["download_count"]."</td><td>".$req_row["response_time"]."</td><td>".$req_row["city"]."</td><td>".$req_row["region_name"]."</td><td>".$req_row["country_name"]."</td><td>".$req_row["generation_region"]."</td><td>".$req_row["Scale_Factor"]."</td><td>".$req_row["Beginning_Moving_Objects"]."</td><td>".$req_row["Beginning_External_Objects"]."</td><td>".$req_row["Moving_Objects_Per_Time_Unit"]."</td><td>".$req_row["External_Objects_Per_Time_Unit"]."</td><td>".$req_row["Classes_Count_of_Moving_Objects"]."</td><td>".$req_row["Classes_Count_of_External_Objects"]."</td><td>".$req_row["Time_Units"]."</td><td>".$req_row["Report_Probability"]."</td><td>".$req_row["Max_Speed_Div"]."</td>";
	echo "</tr>";
}
echo "</table>";
?>
</td>
</tr>

</table>
</body>
</html>
