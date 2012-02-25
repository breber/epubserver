function getListOfBooks() {
	$.getJSON("/book/list", function(results) {
		var tbl = document.getElementById("availableBooks");

		$.each(results.books, function(index, data) {
			console.log(data);
			var row = document.createElement("div");
			row.className = "row-fluid";
			
			var imgDiv = document.createElement("div");
			var img = document.createElement("img");
			img.src = data.cover;

			imgDiv.className = "span2";
			imgDiv.appendChild(img);
			row.appendChild(imgDiv);
			
			var td = document.createElement("div");
			td.className = "span10";
			var lastReadDate = new Date(data.lastRead);
			var lastHour = lastReadDate.getHours();
			var lastMin = lastReadDate.getMinutes();
			
			var lastRead = (lastReadDate.getMonth() + 1) + "/" + lastReadDate.getDate() + "/" + lastReadDate.getFullYear() + " " + 
								((lastHour > 12) ? (lastHour - 12) : lastHour) + ":" + 
								((lastMin < 10) ? ("0" + lastMin) : lastMin) + " " + ((lastHour > 12) ? "PM" : "AM");
			
			if (data.currentPlace === undefined) {
				var link = document.createElement("a");
				link.href = '/book.html?bookId=' + data.bookid + '&bookTitle=' + window.encodeURI(data.title);
				link.textContent = data.title;
				
				td.appendChild(link);
				
				var lastReadDiv = document.createElement("div");
				lastReadDiv.className = "lastRead";
				lastReadDiv.textContent = lastRead;
				td.appendChild(lastReadDiv);
			} else {
				var link = document.createElement("a");
				link.href = '/book.html?bookId=' + data.bookid + '&bookTitle=' + window.encodeURI(data.title) + '#' + data.currentPlace;
				link.textContent = data.title;
				
				td.appendChild(link);
				
				var lastReadDiv = document.createElement("div");
				lastReadDiv.className = "lastRead";
				lastReadDiv.textContent = lastRead;
				td.appendChild(lastReadDiv);
			}
			
			row.appendChild(td);
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
				li.innerHTML = '<a id="link' + data.id + ';' + data.id_end + '" href="#' + data.id + ';' + data.id_end + '">' + data.title + '</a>';
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
