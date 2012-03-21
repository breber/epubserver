function getListOfBooks() {
	$.getJSON("/book/list", function(results) {
		var tbl = document.getElementById("availableBooks");

		$.mobile.hidePageLoadingMsg();
		
		$.each(results.books, function(index, data) {
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
			
			var tempUrl = "/book.html?bookId=" + data.bookid + "&bookTitle=" + window.encodeURI(data.title);
			
			if (data.currentPlace !== undefined) {
				tempUrl += data.currentPlace;
			}
			
			anchor.href = tempUrl;
			
			anchor.appendChild(img);
			anchor.appendChild(heading);
			anchor.appendChild(lastReadP);
			
			row.appendChild(anchor);
			tbl.appendChild(row);
		});
		
		$('#availableBooks').listview('refresh');
	});
};

function getResources() {
	var bookId = $.getUrlVar('bookId');
	var bookTitle = $.getUrlVar('bookTitle');
	var tempUrl = "/book.html?bookId=" + bookId + "&bookTitle=" + bookTitle;
	
	$("#bookTitle").text(window.decodeURI(bookTitle));
	
	if (bookId !== null && bookId !== undefined) {
		var tbl = document.getElementById("chapterGuide");
		
		$.getJSON("/book/resources", { bookid : bookId }, function(results) {
			$.each(results.resources, function(index, data) {
				var li = document.createElement("li");
				var href = tempUrl + "&curRes=" + data.id + "&endRes=" + data.id_end;
				
				li.innerHTML = '<a id="link' + data.id + ';' + data.id_end + '" href="' + href + '" data-ajax="false">' + data.title + '</a>';
				
				tbl.appendChild(li);
			});
			
			// If we have a hash value in our url, try and load those resources
			var currentResource = $.getUrlVar('curRes');
			var endResource = $.getUrlVar('endRes');
		
			if (currentResource !== undefined && currentResource !== null) {
				loadResource(currentResource, endResource);
			}

			$('#chapterGuide').listview('refresh');
		});
	}
};

function loadResource(start, end) {
	var bookId = $.getUrlVar('bookId');
	
	if (bookId !== null && bookId !== undefined) {
		var content = document.getElementById("content");

		$("#chapterGuide").ready(function() {
			$("li.active").removeClass("active");
			$("li[id='" + start + ";" + end + "']").addClass("active");
		});
		content.src = "http://" + window.location.host + "/book/content?bookid=" + bookId + 
							"&startid=" + start + "&endid=" + end;
	}
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
