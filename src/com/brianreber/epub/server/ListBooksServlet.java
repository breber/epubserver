package com.brianreber.epub.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
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
			@SuppressWarnings("unchecked")
			List<Book> records = (List<Book>) pm.newQuery(Book.class).execute();

			JSONObject obj = new JSONObject();
			JSONArray names = new JSONArray();

			for (Book b : records) {
				JSONObject tmp = new JSONObject();
				tmp.put("title", b.getTitle());
				tmp.put("bookid", b.getId());
				names.put(tmp);
			}

			obj.put("books", names);

			String output = obj.toString();
			out.print(output);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
