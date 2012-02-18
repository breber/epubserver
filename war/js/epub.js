function getListOfBooks() {
	$.getJSON("/book/list", function(results) {
		var tbl = document.getElementById("availableBooks");

		$.each(results.books, function(index, data) {
			console.log(data);
			var row = document.createElement("tr");
			
			var td1 = document.createElement("td");
			var img = document.createElement("img");
			img.src = data.cover;
			td1.appendChild(img);
			row.appendChild(td1);
			
			var td = document.createElement("td");
			if (data.currentPlace === undefined) {
				td.innerHTML = "<a href='/book.html?bookId=" + data.bookid + "'>" + data.title + "</a>";
			} else {
				td.innerHTML = "<a href='/book.html?bookId=" + data.bookid + "#" + data.currentPlace + "'>" + data.title + "</a>";
			}
			
			row.appendChild(td);
			tbl.appendChild(row);
		});
	});
};

function getResources() {
	var bookId = $.getUrlVar('bookId');
	
	if (bookId !== null && bookId !== undefined) {
		var tbl = document.getElementById("chapterGuide");
		
		$.getJSON("/book/resources", { bookid : bookId }, function(results) {
			$.each(results.resources, function(index, data) {
				var row = document.createElement("tr");
				var td = document.createElement("td");

				td.innerHTML = '<a id="' + data.id + ';' + data.id_end + '" href="#' + data.id + ';' + data.id_end + '">' + data.title + '</a>';
				td.onclick = function() {
					loadResource(data.id, data.id_end);
				};

				row.appendChild(td);
				tbl.appendChild(row);
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
			$("a.selected").removeClass("selected");
			$("a[id='" + start + ";" + end + "']").addClass("selected");
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
