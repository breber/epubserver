package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class GetBookContentServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(GetBookContentServlet.class.getSimpleName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		res.setContentType("text/html; charset=UTF-8");

		PrintWriter out = res.getWriter();

		String bookId = req.getParameter("bookid");
		int startIndex = Integer.parseInt(req.getParameter("startid"));
		int endIndex = Integer.parseInt(req.getParameter("endid"));

		log.log(Level.WARNING, "start: " + startIndex + "; end: " + endIndex);

		try {
			String blobId = updateBook(bookId, "&curRes=" + startIndex + "&endRes=" + endIndex);
			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(blobId));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			Spine spine = b.getSpine();

			StringBuilder sb = new StringBuilder();

			for (int i = startIndex; i < endIndex; i++) {
				Resource r = spine.getResource(i);
				String data = AppEngineUtil.readStreamAsString(r.getInputStream());
				String href = r.getHref();

				data = data.replaceAll("href=\"(.+?)\"", "href=\"/book/getresource?bookid=" + bookId + "&base=" + href + "&other=$1\"");
				data = data.replaceAll("src=\"(.+?)\"", "href=\"/book/getresource?bookid=" + bookId + "&base=" + href + "&other=$1\"");
				log.log(Level.SEVERE, "DataPost= " + data);

				sb.append(data);
			}

			out.print(sb.toString());
		} finally {
			pm.close();
		}
	}

	private String updateBook(String bookId, String resource) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction tr = pm.currentTransaction();
		String toRet = "";

		try {
			tr.begin();

			Key key = KeyFactory.createKey("Book", Long.parseLong(bookId));
			Entity result = datastore.get(key);

			log.log(Level.SEVERE, "result ==  " + result);

			if (result != null) {
				result.setProperty("status", Book.INPROGRESS);
				result.setProperty("currentResource", resource);
				result.setProperty("lastRead", new Date());
				datastore.put(result);

				toRet = (String) result.getProperty("blobId");
			}

			tr.commit();
		} catch (Exception e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));

			if (tr.isActive()) {
				tr.rollback();
			}

			pm.close();
		}

		return toRet;
	}
}
