package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;


@SuppressWarnings("serial")
public class GetBookResourceServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(GetBookResourceServlet.class.getSimpleName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PrintWriter out = res.getWriter();

		String bookId = req.getParameter("bookid");
		String resHref = req.getParameter("href");

		log.log(Level.SEVERE, "GetResource: bookid = " + bookId + "; resHref = " + resHref);

		try {
			Book book = pm.getObjectById(Book.class, Long.parseLong(bookId));

			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(book.getBlobId()));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			Resource r = b.getResources().getByHref(resHref);
			String data = AppEngineUtil.readStreamAsString(r.getInputStream());

			res.setContentType(r.getMediaType().getName());

			log.log(Level.SEVERE, "GetResource: " + r);
			log.log(Level.SEVERE, "GetResource: " + r.getMediaType().getName());
			log.log(Level.SEVERE, "GetResource: " + data);

			out.print(data);
		} finally {
			pm.close();
		}
	}
}
