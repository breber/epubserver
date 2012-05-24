<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="http://cdn.brianreber.com/css/jquery.mobile.min.css" type="text/css" />
<script type="text/javascript" src="http://cdn.brianreber.com/js/jquery.min.js"></script>
<script type="text/javascript" src="http://cdn.brianreber.com/js/jquery.mobile.min.js"></script>

<title>Upload an ePub Book</title>
</head>

<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<body>
	<div data-role="page">
		<div data-role="header">
			<a href="/" data-icon="home" data-iconpos="notext">Home</a>
			<h1>Upload ePub</h1>
		</div>
		<div data-role="content">
			<form action="<%=blobstoreService.createUploadUrl("/book/upload")%>"
				method="post" enctype="multipart/form-data" data-ajax="false">
				<input type="file" name="book" />
				<input type="submit" value="Submit" />
			</form>
		</div>
	</div>
</body>
</html>