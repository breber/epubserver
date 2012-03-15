function getListOfBooks() {
	$.getJSON("/book/list", function(results) {
		var tbl = document.getElementById("availableBooks");

		$.each(results.books, function(index, data) {
			console.log(data);
			var row = document.createElement("li");
			row.className = "ui-btn ui-li ui-li-has-thumb";
			row.setAttribute("data-inline", "false");
			row.setAttribute("data-theme", "c");
			
			var rowDiv = document.createElement("div");
			rowDiv.className = "ui-btn-inner ui-li";
			
			var img = document.createElement("img");
			img.src = data.cover;
			img.className = "ui-li-thumb";

			var td = document.createElement("a");
			td.className = "ui-link-inherit";
			td.setAttribute("data-ajax", "false");
			td.appendChild(img);

			var lastReadDate = new Date(data.lastRead);
			var lastHour = lastReadDate.getHours();
			var lastMin = lastReadDate.getMinutes();
			
			var lastRead = (lastReadDate.getMonth() + 1) + "/" + lastReadDate.getDate() + "/" + lastReadDate.getFullYear() + " " + 
								((lastHour > 12) ? (lastHour - 12) : lastHour) + ":" + 
								((lastMin < 10) ? ("0" + lastMin) : lastMin) + " " + ((lastHour > 12) ? "PM" : "AM");
			
			if (data.currentPlace === undefined) {
				var link = document.createElement("h3");
				link.textContent = data.title;
				link.className = "ui-li-heading";
				
				td.href = '/book.html?bookId=' + data.bookid + '&bookTitle=' + window.encodeURI(data.title);
				td.appendChild(link);
				
				var lastReadP = document.createElement("p");
				lastReadP.className = "ui-li-desc";
				lastReadP.textContent = lastRead;
				td.appendChild(lastReadP);
			} else {
				var link = document.createElement("h3");
				link.textContent = data.title;
				link.className = "ui-li-heading";
				
				td.href = '/book.html?bookId=' + data.bookid + '&bookTitle=' + window.encodeURI(data.title) + '#' + data.currentPlace;
				td.appendChild(link);
				
				var lastReadP = document.createElement("p");
				lastReadP.className = "ui-li-desc";
				lastReadP.textContent = lastRead;
				td.appendChild(lastReadP);
			}
			
			rowDiv.appendChild(td);
			row.appendChild(rowDiv);
			tbl.appendChild(row);
		});
	});
};

function getResources() {
	var bookId = $.getUrlVar('bookId');
	var bookTitle = $.getUrlVar('bookTitle');
	
	$("#bookTitle").text(window.decodeURI(bookTitle));
	
	if (bookId !== null && bookId !== undefined) {
		var tbl = document.getElementById("chapterGuide");
		
		$.getJSON("/book/resources", { bookid : bookId }, function(results) {
			$.each(results.resources, function(index, data) {
				var li = document.createElement("li");

				li.id = data.id + ';' + data.id_end;
				li.className = "ui-btn ui-li";
				li.setAttribute("data-theme", "c");
				
				var div = document.createElement("div");
				div.className = "ui-btn-inner ui-li";
				
				var div1 = document.createElement("div");
				div1.innerHTML = '<a id="link' + data.id + ';' + data.id_end + '" href="#' + data.id + ';' + data.id_end + '" class="ui-link-inherit" data-ajax="false">' + data.title + '</a>';
				div1.className = "ui-btn-text";
				
				div.appendChild(div1);
				li.appendChild(div);
				li.onclick = function() {
					loadResource(data.id, data.id_end);
				};

				tbl.appendChild(li);
			});
			
			// If we have a hash value in our url, try and load those resources
			var urlHash = window.location.hash;
			if (urlHash !== null && urlHash.length > 1) {
				var currentResource = urlHash.substr(1, urlHash.indexOf(';') - 1);
				var endResource = urlHash.substr(urlHash.indexOf(';') + 1);
			
				if (currentResource !== undefined && currentResource !== null) {
					loadResource(currentResource, endResource);
				}
			}
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
