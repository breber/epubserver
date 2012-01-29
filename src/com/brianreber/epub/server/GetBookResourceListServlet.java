package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;


@SuppressWarnings("serial")
public class GetBookResourceListServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PrintWriter out = res.getWriter();
		res.setContentType("application/json");

		String bookId = req.getParameter("bookid");

		try {
			Book book = pm.getObjectById(Book.class, Long.parseLong(bookId));

			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(book.getBlobId()));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			JSONObject obj = new JSONObject();

			TableOfContents resources = b.getTableOfContents();
			Spine spine = b.getSpine();

			obj.put("resources", getTableOfContents(resources.getTocReferences(), spine));

			out.print(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}finally {
			pm.close();
		}
	}

	/**
	 * Recursively Log the Table of Contents
	 *
	 * @param tocReferences
	 * @param depth
	 * @throws JSONException
	 */
	private JSONArray getTableOfContents(List<TOCReference> tocReferences, Spine spine) throws JSONException {
		if (tocReferences == null) {
			return null;
		}
		JSONArray arr = new JSONArray();

		for (int i = 0; i < tocReferences.size(); i++) {
			TOCReference tocReference = tocReferences.get(i);
			JSONObject obj = new JSONObject();

			int startingId = spine.getResourceIndex(tocReference.getResource());
			int endId = startingId;

			try {
				TOCReference next = tocReferences.get(i + 1);
				endId = spine.getResourceIndex(next.getResource());
			} catch (IndexOutOfBoundsException ex) {
				endId = spine.size();
			}

			obj.append("id", startingId);
			obj.append("id_end", endId);

			obj.append("title", tocReference.getTitle());

			arr.put(obj);
		}

		return arr;
	}
}
