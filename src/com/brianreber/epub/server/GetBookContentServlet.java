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
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;

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
			Book book = pm.getObjectById(Book.class, Long.parseLong(bookId));

			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(book.getBlobId()));

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
}
