<?php
function handle_error($error_str)
{
}
function get_stats_db_info() {
	$db_info = array();
	$db_info['db_name'] = "traffic";
	$db_info['dbserver'] = "localhost";
	$db_info['dbusername'] = "traffic";
	$db_info['dbpassword'] = "thetrafficgenerator";
	return $db_info;
}
function get_combo_requests_stats($outputfilepath) {

	$db_info = get_stats_db_info();
	$dbname = $db_info['db_name'];
	$tablename = $tablename;
	$dbserver = $db_info['dbserver'];
	$dbusername = $db_info['dbusername'];
	$dbpassword = $db_info['dbpassword'];
	
	//connect to stats db
	$db  =  mysql_connect ( $dbserver , $dbusername , $dbpassword );
	mysql_select_db ( $dbname , $db );
	//end connect to stats db

	//get and save statistics
	$stats_str = "";
	$req_total = 0;
	$sql  =  "SELECT stats_table.request_id, email, stats_table.hostname, stats_table.received request_timestamp, (SELECT count(download_table.id) FROM stats_traffic_downloads download_table WHERE download_table.request_id = stats_table.request_id) download_count, ROUND(TIMESTAMPDIFF(SECOND,tr_table.created,tr_table.finished_timestamp)/60,2) response_time, ip_table.city, ip_table.region_name, ip_table.country_name, upperlat generation_region_upperlat, upperlong generation_region_upperlong, lowerlat generation_region_lowerlat, lowerlong generation_region_lowerlong, scaleFactor Scale_Factor, objBegin Beginning_Moving_Objects, extObjBegin Beginning_External_Objects, objPerTime Moving_Objects_Per_Time_Unit, extObjPerTime External_Objects_Per_Time_Unit, numObjClasses Classes_Count_of_Moving_Objects, numExtObjClasses Classes_Count_of_External_Objects, maxTime Time_Units, reportProb Report_Probability, msd Max_Speed_Div FROM stats_traffic_requests stats_table, ip ip_table, traffic_requests tr_table WHERE ip_table.ip = stats_table.ip AND stats_table.request_id = tr_table.id";
	
	$results  =  mysql_query ( $sql);
	if(!$results)
		handle_error(mysql_error());
	else {
		
		$outfile = fopen($outputfilepath,"w");
		if(!$outfile)
			echo "Error creating file ".$outputfilepath."<br/>";
		//header line
		$line = "";
		//$line = $line."\"request_id\",\"email\",\"hostname\",\"request_timestamp\",\"download_count\",\"response_time\",\"city\",\"region_name\",\"country_name\",\"generation_region_upperlat\",\"generation_region_upperlong\",\"generation_region_lowerlat\",\"generation_region_lowerlong\",\"Scale_Factor\",\"Beginning_Moving_Objects\",\"Beginning_External_Objects\",\"Moving_Objects_Per_Time_Unit\",\"External_Objects_Per_Time_Unit\",\"Classes_Count_of_Moving_Objects\",\"Classes_Count_of_External_Objects\",\"Time_Units\",\"Report_Probability\",\"Max_Speed_Div\"";
		$line = $line."\"request_id\",\"email\",\"hostname\",\"request_timestamp\",\"download_count\",\"response_time\",\"city\",\"region_name\",\"country_name\",\"generation_area\",\"Scale_Factor\",\"Beginning_Moving_Objects\",\"Beginning_External_Objects\",\"Moving_Objects_Per_Time_Unit\",\"External_Objects_Per_Time_Unit\",\"Classes_Count_of_Moving_Objects\",\"Classes_Count_of_External_Objects\",\"Time_Units\",\"Report_Probability\",\"Max_Speed_Div\"";
		$line = $line."\n";
		fwrite($outfile,$line);
		$stats_str = $stats_str.$line."<br/>";
		//end header line
		while($req_row = mysql_fetch_assoc($results)){
			$n = floatval($req_row["generation_region_upperlat"]);
			$w = floatval($req_row["generation_region_upperlong"]);
			$s = floatval($req_row["generation_region_lowerlat"]);
			$e = floatval($req_row["generation_region_lowerlong"]);
			$avgLat = ($n+$s)/2;
			$avgLng = ($e+$w)/2;
			//echo $s.",".$w."<br/>";
			//echo $n.",".$e."<br/>";
			$url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=".$avgLat.",".$avgLng."&sensor=false&bounds=".$s.",".$w."|".$n.",".$e;
			$geocode_response = file_get_contents($url);

			$geocode_array = json_decode($geocode_response, true);
			//print_r($geocode_array["results"][0]["formatted_address"]);
			//echo $geocode_response;
			$generation_region = $geocode_array["results"][0]["formatted_address"];
			//$generation_region = "dummy region";
			$line = "";
			$line = $line."\"".$req_row["request_id"]."\",\"".$req_row["email"]."\",\"".$req_row["hostname"]."\",\"".$req_row["request_timestamp"]."\",\"".$req_row["download_count"]."\",\"".$req_row["response_time"]."\",\"".$req_row["city"]."\",\"".$req_row["region_name"]."\",\"".$req_row["country_name"]."\",\"".$generation_region."\",\"".$req_row["Scale_Factor"]."\",\"".$req_row["Beginning_Moving_Objects"]."\",\"".$req_row["Beginning_External_Objects"]."\",\"".$req_row["Moving_Objects_Per_Time_Unit"]."\",\"".$req_row["External_Objects_Per_Time_Unit"]."\",\"".$req_row["Classes_Count_of_Moving_Objects"]."\",\"".$req_row["Classes_Count_of_External_Objects"]."\",\"".$req_row["Time_Units"]."\",\"".$req_row["Report_Probability"]."\",\"".$req_row["Max_Speed_Div"]."\"";
			$line = $line."\n";
			fwrite($outfile,$line);
			$stats_str = $stats_str.$line."<br/>";
		}
		fflush($outfile);
		fclose($outfile);
	}
	//end get and save statistics
	return $stats_str;
}
function get_stats($tablename, $outputfilepath) {
	$db_info = get_stats_db_info();
	$dbname = $db_info['db_name'];
	$tablename = $tablename;
	$dbserver = $db_info['dbserver'];
	$dbusername = $db_info['dbusername'];
	$dbpassword = $db_info['dbpassword'];
	
	//connect to stats db
	$db  =  mysql_connect ( $dbserver , $dbusername , $dbpassword );
	mysql_select_db ( $dbname , $db );
	//end connect to stats db

	//get and save statistics
	$stats_str = "";
	$req_total = 0;
	$sql  =  "SELECT hostname, received timestamp, city, region_name, country_name, browser FROM $tablename stats_table, ip WHERE ip.ip = stats_table.ip" ;
	
	$results  =  mysql_query ( $sql);
	if(!$results)
		handle_error(mysql_error());
	else {
		
		$outfile = fopen($outputfilepath,"w");
		if(!$outfile)
			echo "Error creating file ".$outputfilepath."<br/>";
		//header line
		$line = "";
		$line = $line."\""."hostname"."\","."\""."timestamp"."\","."\""."city"."\","."\""."region_name"."\","."\""."country_name"."\","."\""."browser"."\"";
		$line = $line."\n";
		fwrite($outfile,$line);
		$stats_str = $stats_str.$line."<br/>";
		//end header line
		while($req_row = mysql_fetch_assoc($results)){
			$line = "";
			$line = $line."\"".$req_row["hostname"]."\","."\"".$req_row["timestamp"]."\","."\"".$req_row["city"]."\","."\"".$req_row["region_name"]."\","."\"".$req_row["country_name"]."\","."\"".$req_row["browser"]."\"";
			$line = $line."\n";
			fwrite($outfile,$line);
			$stats_str = $stats_str.$line."<br/>";
		}
		fflush($outfile);
		fclose($outfile);
	}
	//end get and save statistics
	return $stats_str;
}

function get_count($tablename, $isunique){
	$db_info = get_stats_db_info();
	$dbname = $db_info['db_name'];
	$tablename = $tablename;
	$dbserver = $db_info['dbserver'];
	$dbusername = $db_info['dbusername'];
	$dbpassword = $db_info['dbpassword'];
	
	//connect to stats db
	$db  =  mysql_connect ( $dbserver , $dbusername , $dbpassword );
	mysql_select_db ( $dbname , $db );
	//end connect to stats db
	
	//get statistics
	$req_total = 0;
	if($isunique)
		$sql  =  "SELECT count(distinct ip) req_total FROM $tablename" ;
	else
		$sql  =  "SELECT count(*) req_total FROM $tablename" ;
	$results  =  mysql_query ( $sql);
	if(!$results)
		handle_error(mysql_error());
	else {
		if(mysql_num_rows ($results) != 0){
			$req_row = mysql_fetch_assoc($results);
			$req_total = $req_row["req_total"];
		}
	}
	//end get statistics
	return $req_total;
}
function get_count_by_country($tablename, $isunique){
$db_info = get_stats_db_info();
	$dbname = $db_info['db_name'];
	$tablename = $tablename;
	$dbserver = $db_info['dbserver'];
	$dbusername = $db_info['dbusername'];
	$dbpassword = $db_info['dbpassword'];
	
	//connect to stats db
	$db  =  mysql_connect ( $dbserver , $dbusername , $dbpassword );
	mysql_select_db ( $dbname , $db );
	//end connect to stats db
	
	//get statistics
	$statistics = array();
	if($isunique)
		$sql  =  "SELECT count(distinct $tablename.ip) count, country_name FROM $tablename, ip where ip.ip = $tablename.ip group by country_name order by country_name" ;
	else
		$sql  =  "SELECT count(id) count, country_name FROM $tablename, ip where ip.ip = $tablename.ip group by country_name order by country_name" ;
	$results  =  mysql_query ( $sql);
	if(!$results)
		handle_error(mysql_error());
	else {
		if(mysql_num_rows ($results) != 0){
			
			while($req_row = mysql_fetch_assoc($results))
				array_push($statistics, $req_row);
		}
	}
	//end get statistics
	return $statistics;
}
?>