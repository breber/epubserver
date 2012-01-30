package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.epub.EpubReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;


@SuppressWarnings("serial")
public class GetBookContentServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(GetBookContentServlet.class.getSimpleName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PrintWriter out = res.getWriter();
		res.setContentType("application/json");

		String bookId = req.getParameter("bookid");
		int startIndex = Integer.parseInt(req.getParameter("startid"));
		int endIndex = Integer.parseInt(req.getParameter("endid"));

		log.log(Level.WARNING, "start: " + startIndex + "; end: " + endIndex);

		try {
			Book book = pm.getObjectById(Book.class, Long.parseLong(bookId));

			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(book.getBlobId()));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			JSONObject obj = new JSONObject();
			JSONArray arr = new JSONArray();

			Spine spine = b.getSpine();

			for (int i = startIndex; i < endIndex; i++) {
				Resource r = spine.getResource(i);
				String data = AppEngineUtil.readStreamAsString(r.getInputStream());
				String href = r.getHref();

				log.log(Level.SEVERE, "HREF = " + href);
				log.log(Level.SEVERE, "DataPre = " + data);

				String newHref = href.contains("/") ? href.substring(0, href.lastIndexOf('/')) : "";

				if (data.contains("href=\"../")) {
					if (newHref.contains("/")) {
						newHref = newHref.substring(0, href.lastIndexOf('/'));
					} else {
						newHref = "";
					}

					data = data.replaceAll("href=\"../", "href=\"/book/getresource?bookid=" + bookId + "&href=" + newHref);
				} else if (data.contains("href=")) {
					data = data.replaceAll("href=\"", "href=\"/book/getresource?bookid=" + bookId + "&href=" + newHref);
				}
				log.log(Level.SEVERE, "DataPost= " + data);

				JSONObject tmp = new JSONObject();
				tmp.put("id", r.getId());
				tmp.put("data", data);
				arr.put(tmp);
			}

			obj.put("resources", arr);

			out.print(obj.toString());
		} catch (JSONException e) {
			log.log(Level.SEVERE, "caught exception " + e.getMessage());
			log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));
		} finally {
			pm.close();
		}
	}
}
