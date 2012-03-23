function getListOfBooks() {
	$.getJSON("/book/list", function(results) {
		var tbl = document.getElementById("availableBooks");
		
		$.mobile.hidePageLoadingMsg();
		
		if (results.inprogress !== null && results.inprogress !== undefined && results.inprogress.length > 0) {
			addListDivider(tbl, "In Progress");
			$.each(results.inprogress, function(index, data) {
				addBookToList(tbl, data);
			});
		}
		
		if (results.queue !== null && results.queue !== undefined && results.queue.length > 0) {
			addListDivider(tbl, "Queue");
			$.each(results.queue, function(index, data) {
				addBookToList(tbl, data);
			});
		}
		
		if (results.finished !== null && results.finished !== undefined && results.finished.length > 0) {
			addListDivider(tbl, "Previously Read");
			$.each(results.finished, function(index, data) {
				addBookToList(tbl, data);
			});
		}
		
		$('#availableBooks').listview('refresh');
	});
};

function addListDivider(tbl, title) {
	var row = document.createElement("li");
	
	row.setAttribute("data-role", "list-divider");
	row.textContent = title;
	
	tbl.appendChild(row);
};

function addBookToList(tbl, data) {
	var row = document.createElement("li");
	var anchor = document.createElement("a");
	var img = document.createElement("img");
	var heading = document.createElement("h3");
	var lastReadP = document.createElement("p");
	
	var lastReadDate = new Date(data.lastRead);
	var lastHour = lastReadDate.getHours();
	var lastMin = lastReadDate.getMinutes();
	
	var lastRead = (lastReadDate.getMonth() + 1) + "/" + lastReadDate.getDate() + "/" + lastReadDate.getFullYear() + " " + 
						((lastHour > 12) ? (lastHour - 12) : lastHour) + ":" + 
						((lastMin < 10) ? ("0" + lastMin) : lastMin) + " " + ((lastHour > 12) ? "PM" : "AM");
	
	img.src = data.cover;
	heading.textContent = data.title;
	lastReadP.textContent = lastRead;
	anchor.setAttribute("data-ajax", "false");
	row.setAttribute("data-icon", "arrow-r");
	row.setAttribute("data-iconpos", "right");
	row.setAttribute("data-inline", "false");
	row.setAttribute("data-wrapperels", "div");
	
	var tempUrl = "/book.html?bookId=" + data.bookid + "&bookTitle=" + window.encodeURI(data.title).replace('#', '%23');
	
	if (data.currentPlace !== undefined && data.currentPlace.length > 0) {
		tempUrl += data.currentPlace;
	} else {
		tempUrl += "#chapterList";
	}
	
	anchor.href = tempUrl;
	
	anchor.appendChild(img);
	anchor.appendChild(heading);
	anchor.appendChild(lastReadP);
	
	row.appendChild(anchor);
	tbl.appendChild(row);
};

function getResources() {
	var bookId = $.getUrlVar('bookId');
	var bookTitle = $.getUrlVar('bookTitle');
	var tempUrl = "/book.html?bookId=" + bookId + "&bookTitle=" + bookTitle;
	
	$("#titleBookPage").text(window.decodeURI(bookTitle).replace('%23', '#'));
	
	if (bookId !== null && bookId !== undefined) {
		var tbl = document.getElementById("chapterGuide");
		var currentResource = $.getUrlVar('curRes');
		var endResource = $.getUrlVar('endRes');
		
		$.getJSON("/book/resources", { bookid : bookId }, function(results) {
			$.mobile.hidePageLoadingMsg();
			
			var foundNext = false;
			var foundPrev = false;
			
			$.each(results.resources, function(index, data) {
				var li = document.createElement("li");
				var href = tempUrl + "&curRes=" + data.id + "&endRes=" + data.id_end;
				
				li.innerHTML = '<a id="link' + data.id + ';' + data.id_end + '" href="' + href + '" data-ajax="false">' + data.title + '</a>';
				
				tbl.appendChild(li);
				
				if (data.id == endResource) {
					foundNext = true;
					$("#next .ui-btn-text").text(String(data.title));
					$("#next").attr("href", href);
				}
				
				if (data.id == currentResource) {
					$("#current .ui-btn-text").text(String(data.title));
				}
				
				if (data.id_end == currentResource) {
					foundPrev = true;
					$("#previous .ui-btn-text").text(String(data.title));
					$("#previous").attr("href", href);
				}
			});
			
			try {
				$('#chapterGuide').listview('refresh');
			} catch (e) {
				// Do nothing...
			}
			
			// If we didn't find a "next", set it to finish book on click
			if (!foundNext) {
				$("#next .ui-btn-text").text("Finish");
				$("#next").attr("href", "/book/finish?bookId=" + bookId);
			}
			
			// If we didn't find a "previous", set it to the chapter list
			if (!foundPrev) {
				$("#previous .ui-btn-text").text("Chapters");
				$("#previous").attr("href", "#chapterList");
			}
		});
	}
};

function loadResource(start, end) {
	var bookId = $.getUrlVar('bookId');
	
	if (bookId !== null && bookId !== undefined) {
		var content = document.getElementById("content");

		content.src = "http://" + window.location.host + "/book/content?bookid=" + bookId + 
							"&startid=" + start + "&endid=" + end;
	}
	
	$.mobile.hidePageLoadingMsg();
};

$.extend({
    getUrlVars: function(){
        var vars = [], hash;
        var hashes = window.location.search.slice(1).split('&');
        for (var i = 0; i < hashes.length; i++) {
            hash = hashes[i].split('=');
            vars.push(hash[0]);
            vars[hash[0]] = hash[1];
        }
        return vars;
    },
    getUrlVar: function(name){
        return $.getUrlVars()[name];
    }
});
