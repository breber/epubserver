package com.brianreber.epub.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.siegmann.epublib.epub.EpubReader;

import org.apache.geronimo.mail.util.Base64;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;

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
			if (b.getCoverImage() != null) {
				book.setCoverImage(scaleCover(b.getCoverImage().getInputStream()));
			} else {
				book.setCoverImage("");
			}

			try {
				pm.makePersistent(book);
			} finally {
				pm.close();
			}
		}

		resp.sendRedirect("/");
	}
	private static final Logger log = Logger.getLogger(GetBookContentServlet.class.getSimpleName());
	private String scaleCover(InputStream stream) {
		ByteArrayInputStream is = (ByteArrayInputStream) stream;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		try {
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] bytes = buffer.toByteArray();

		Image img = ImagesServiceFactory.makeImage(bytes);
		Transform resize = ImagesServiceFactory.makeResize(50, 100);
		log.log(Level.SEVERE, "Image height: " + img.getHeight());
		log.log(Level.SEVERE, "Image width: " + img.getWidth());
		log.log(Level.SEVERE, "Image format: " + img.getFormat());
		img = ImagesServiceFactory.getImagesService().applyTransform(resize, img);
		String imgData = new String(Base64.encode(img.getImageData()));

		// Return the base64 encoded image data
		return "data:image/" + img.getFormat().toString() + ";base64," + imgData;
	}
}
