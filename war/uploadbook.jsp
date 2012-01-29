<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<html>
	<head>
		<title>Upload an ePub Book</title>
	</head>
	
	<%
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	%>
	
	<body>
	    <form action="<%= blobstoreService.createUploadUrl("/book/upload") %>" method="post" enctype="multipart/form-data">
	        <input type="file" name="book">
	        <input type="submit" value="Submit">
	    </form>
	</body>
</html>