<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
	<link rel="stylesheet" href="css/bootstrap-responsive.min.css" type="text/css" />
	<link rel="stylesheet" href="css/website.css" type="text/css" />
	<script type="text/javascript" src="js/jquery.min.js"></script>
	<script type="text/javascript" src="js/bootstrap.min.js"></script>
	<title>Upload an ePub Book</title>
</head>

<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>

<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="brand" href="/">ePub Reader</a>
			</div>
		</div>
	</div>
	<div class="container page">
		<div class="row-fluid">
			<form action="<%= blobstoreService.createUploadUrl("/book/upload") %>"
				method="post" enctype="multipart/form-data">
				<input type="file" name="book" />
				<input type="submit" value="Submit" />
			</form>
		</div>
  	</div>
</body>
</html>