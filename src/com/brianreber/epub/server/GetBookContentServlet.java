package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

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
		String resId = req.getParameter("resId");

		log.log(Level.WARNING, "resNum: " + resId);

		try {
			Book book = pm.getObjectById(Book.class, Long.parseLong(bookId));

			BlobstoreInputStream blob = new BlobstoreInputStream(new BlobKey(book.getBlobId()));

			EpubReader epubReader = new EpubReader();
			nl.siegmann.epublib.domain.Book b = epubReader.readEpub(blob);

			JSONObject obj = new JSONObject();

			List<Resource> resources = b.getContents();
			Spine spine = b.getSpine();

			if (resId != null) {
				for (int i = 0; i < resources.size(); i++) {
					Resource r = resources.get(i);
					if (r.getId().contains(resId)) {
						log.log(Level.WARNING, "found resource: " + i);
						int index = spine.getResourceIndex(r);
						log.log(Level.WARNING, "spine index: " + index);

						TableOfContents toc = b.getTableOfContents();
						List<TOCReference> refs = toc.getTocReferences();

						int nextIndex = index;

						for (int j = 0; j < refs.size(); j++) {
							TOCReference ref = refs.get(j);
							log.log(Level.WARNING, "current TOCRef: " + ref.getTitle() + "; " + ref.getResourceId());
							if (ref.getResourceId().contains(resId)) {
								log.log(Level.WARNING, "found matching TOCRef: " + ref.getTitle() + "; " + ref.getResourceId());
								nextIndex = spine.getResourceIndex(refs.get(j + 1).getResource());
								break;
							}
						}

						log.log(Level.WARNING, "next spine index: " + nextIndex);
						StringBuilder data = new StringBuilder();

						for (int j = index; j < nextIndex; j++) {
							Resource temp = spine.getResource(j);
							data.append(AppEngineUtil.readStreamAsString(temp.getInputStream()));
						}

						obj.put("id", r.getId());
						obj.put("data", data.toString());
						break;
					}
				}
			}

			if (resId == null || !obj.has("id")) {
				String data = AppEngineUtil.readStreamAsString(resources.get(0).getInputStream());

				obj.put("id", resources.get(0).getId());
				obj.put("data", data);
			}

			String output = obj.toString();
			out.print(output);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
