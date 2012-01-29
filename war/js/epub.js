function getListOfBooks() {
		$.getJSON("/book/list", function(results) {
			var tbl = document.getElementById("availableBooks");
			console.log(results);
			
			$.each(results.books, function(index, data) {
				var row = document.createElement("tr");
				var td = document.createElement("td");
				
				td.innerHTML = "<a href='/book.html?bookId=" + data.bookid + "'>" + data.title + "</a>";
				
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
				
				td.innerHTML = '<a href="javascript:void(0);">' + data.title + '</a>';
				
				td.onclick = function() { loadResource(data.id); };
				
				row.appendChild(td);
				tbl.appendChild(row);
			});
		});
	}
};

function loadResource(resId) {
	var bookId = $.getUrlVar('bookId');
	
	if (bookId !== null && bookId !== undefined) {
		var content = document.getElementById("content");
		
		$.getJSON("/book/content", { bookid : bookId, resId: String(resId) }, function(results) {
			content.innerHTML = results.data;
		});
	}
};

$.extend({
    getUrlVars: function(){
        var vars = [], hash;
        var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
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