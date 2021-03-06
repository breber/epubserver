package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("serial")
public class ListBooksServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		PrintWriter out = res.getWriter();
		res.setContentType("application/json");

		try {
			Query query = pm.newQuery("select from " + Book.class.getName()
					+ " where emailAddress=='" + AppEngineUtil.getUserEmail() + "' order by lastRead desc");
			@SuppressWarnings("unchecked")
			List<Book> records = (List<Book>) query.execute();

			JSONObject obj = new JSONObject();

			JSONArray current = new JSONArray();
			JSONArray finished = new JSONArray();
			JSONArray queue = new JSONArray();

			for (Book b : records) {
				JSONObject tmp = new JSONObject();
				tmp.put("title", b.getTitle());
				tmp.put("bookid", b.getId());
				tmp.put("cover", b.getCoverImage());
				tmp.put("currentPlace", b.getCurrentResource());
				tmp.put("lastRead", b.getLastRead());

				if (b.getStatus() == Book.QUEUED) {
					queue.put(tmp);
				} else if (b.getStatus() == Book.INPROGRESS) {
					current.put(tmp);
				} else if (b.getStatus() == Book.FINISHED) {
					finished.put(tmp);
				} else {
					b.setStatus(Book.QUEUED);
					queue.put(tmp);
				}
			}

			obj.put("inprogress", current);
			obj.put("finished", finished);
			obj.put("queue", queue);

			String output = obj.toString();
			out.print(output);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
