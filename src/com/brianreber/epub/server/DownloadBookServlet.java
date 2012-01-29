package com.brianreber.epub.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class DownloadBookServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(DownloadBookServlet.class.getSimpleName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));

		log.log(Level.SEVERE, "DownloadBook: " + blobKey.getKeyString());

		res.setContentType("application/epub+zip");
		blobstoreService.serve(blobKey, res);
	}
}
