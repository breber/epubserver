package com.brianreber.epub.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.epub.EpubReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class UploadBookServlet extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)	throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
		List<BlobKey> blobKey = blobs.get("book");

		if (blobKey != null && blobKey.size() != 0) {
			Book book = new Book();
			book.setEmailAddress(AppEngineUtil.getUserEmail());
			book.setBlobId(blobKey.get(0).getKeyString());

			BlobstoreInputStream blob = new BlobstoreInputStream(blobKey.get(0));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			book.setTitle(b.getTitle());
			book.setCoverImage(b.getCoverPage().getData());

			try {
				pm.makePersistent(book);
			} finally {
				pm.close();
			}
		}

		resp.sendRedirect("/");
	}
}
