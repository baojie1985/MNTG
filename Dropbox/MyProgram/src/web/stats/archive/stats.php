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