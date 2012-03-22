package com.brianreber.epub.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class MarkBookFinishedServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(MarkBookFinishedServlet.class.getSimpleName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		String bookId = req.getParameter("bookid");

		try {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Transaction tr = pm.currentTransaction();

			try {
				tr.begin();

				Key key = KeyFactory.createKey("Book", Long.parseLong(bookId));
				Entity result = datastore.get(key);

				log.log(Level.SEVERE, "result ==  " + result);

				if (result != null) {
					result.setProperty("status", Book.FINISHED);
					result.setProperty("lastRead", new Date());
					datastore.put(result);
				}

				tr.commit();
			} catch (Exception e) {
				log.log(Level.SEVERE, "caught exception " + e.getMessage());
				log.log(Level.SEVERE, Arrays.toString(e.getStackTrace()));

				if (tr.isActive()) {
					tr.rollback();
				}

				pm.close();

				res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} finally {
			pm.close();
		}
	}

}
