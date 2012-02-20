/*
 * Copyright (C) 2012 Brian Reber
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms are permitted
 * provided that the above copyright notice and this paragraph are
 * duplicated in all such forms and that any documentation,
 * advertising materials, and other materials related to such
 * distribution and use acknowledge that the software was developed
 * by Brian Reber.
 * THIS SOFTWARE IS PROVIDED 'AS IS' AND WITHOUT ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 * WARRANTIES OF MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.brianreber.epub.server;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.appengine.api.datastore.Blob;

/**
 * Represents an ePub book the user has in the Blobstore
 * 
 * @author brianreber
 */
@Entity
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String emailAddress;
	private String blobId;
	private String title;
	private String currentResource;
	private Blob coverImage;
	private Date lastRead;

	public Book() {
		lastRead = new Date();
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the blobId
	 */
	public String getBlobId() {
		return blobId;
	}

	/**
	 * @param blobId the blobId to set
	 */
	public void setBlobId(String blobId) {
		this.blobId = blobId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the currentResource
	 */
	public String getCurrentResource() {
		return currentResource;
	}

	/**
	 * @param currentResource the currentResource to set
	 */
	public void setCurrentResource(String currentResource) {
		this.currentResource = currentResource;
	}

	/**
	 * @param data the data to set
	 */
	public void setCoverImage(String data) {
		this.coverImage = new Blob(data.getBytes());
	}

	/**
	 * @return the cover image
	 */
	public String getCoverImage() {
		return new String(coverImage.getBytes());
	}

	/**
	 * @return the lastRead
	 */
	public Date getLastRead() {
		return lastRead;
	}
}
