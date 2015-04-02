<?php
$target_path1 = "uploads/";
$target_path2 = "uploads/";
/* Add the original filename to our target path.
Result is "uploads/filename.extension" */




$target_path1 = $target_path1 . basename( $_FILES['uploadedfile1']['name']);
if(move_uploaded_file($_FILES['uploadedfile1']['tmp_name'], $target_path1)) {
	echo "The first file ".  basename( $_FILES['uploadedfile1']['name']).
	" has been uploaded.";
} else{
	echo "There was an error uploading the file, please try again!";
	echo "filename: " .  basename( $_FILES['uploadedfile1']['name']);
	echo "target_path: " .$target_path1;
}

$target_path2 = $target_path2 . basename( $_FILES['uploadedfile2']['name']);
if(move_uploaded_file($_FILES['uploadedfile2']['tmp_name'], $target_path2)) {
	echo "n The second file ".  basename( $_FILES['uploadedfile2']['name']).
	" has been uploaded.";
} else{
	echo "There was an error uploading the file, please try again!";
	echo "filename: " .  basename( $_FILES['uploadedfile2']['name']);
	echo "target_path2: " .$target_path2;
}

$user = $_REQUEST['user'];
echo "n String Parameter send from client side : " . $user;
?>