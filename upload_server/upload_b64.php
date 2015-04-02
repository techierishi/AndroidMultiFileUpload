<?php


$files_json = file_get_contents("php://input");

$obj_arr = json_decode($files_json);

$imgarrs = $obj_arr->images;

for($i=0;$i<count($imgarrs);$i++){
	saveImage($imgarrs[$i]);
}

function saveImage($base64img){

dbgg($base64img,'a');

	$base64img = str_replace('data:image/jpeg;base64,', '', $base64img);
	$data = base64_decode($base64img);
	$file =  'uploads/' . time().'__img.jpg';
	file_put_contents($file, $data);
}

function dbgg($msg,$md='w',$fn='dbgg.html',$tg='No tag'){
	$fo = fopen($fn,$md);
	fwrite($fo,'<br>[================[ '. date('d/m/Y H:m:s').' { '.$tg. ' } ]================]<br>');
	$msg = "<pre>".print_r($msg,true)."</pre>";
	fwrite($fo,$msg);
	fclose($fo);
}
?>