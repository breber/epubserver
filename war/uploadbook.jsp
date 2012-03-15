<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="css/jquery.mobile.min.css" />
    <script type="text/javascript" src="js/jquery.min.js"></script>
    <script type="text/javascript" src="js/epub.js"></script>
	<script src="js/jquery.min.js"></script>
	<script src="js/jquery.mobile.min.js"></script>
	<title>Upload an ePub Book</title>
</head>

<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); %>

<body class="ui-mobile-viewport">
  	<div data-role="page">
		<div data-role="header" class="center">
			<a href="/" data-icon="arrow-l">
				<span class="ui-btn-text">Home</span>
			</a>
			<h1 tabindex="0" role="heading">ePub Reader</h1>
		</div>
		<div data-role="content">
			<form action="<%= blobstoreService.createUploadUrl("/book/upload") %>"
				method="post" enctype="multipart/form-data">
				<input type="file" name="book" />
				<input type="submit" value="Submit" />
			</form>
		</div>
	</div>
</body>
</html>