function navigateTo (title) {
    $('#javadoc-tree ul span').removeClass('clicked');
    $('#javadoc-tree ul span[title="' + title + '"]').addClass('clicked');
    $("#javadoc-content").removeClass("error");
    title = title.replace(/\./g, '/');
    // Loading the javadoc-pages of the selected items into the contentarea
    $("#javadoc-content").load(title + '.html', function (response, status, xhr) {
        if (status == "error") {
            var msg = '<h1>Error</h1><p>Failed to open the javadoc-file for the selected java-object "<code>' + title + '</code>"!</p>';
            $("#javadoc-content").addClass("error").html(msg + '<p>Errorcode:</p><pre>' + xhr.status + " " + xhr.statusText + '</pre>');
        }
    });
}

$(document).ready(function() {
    // Filtering the tree:

    var $tree = $('#javadoc-tree > ul');
    var $searchCheckbox = $('#javadoc-tree > .search > input[type=checkbox]');
    var $searchInput = $('#javadoc-tree > .search > input[type=text]');
    var filterTree = function () {
    	$('li').removeClass('marked found');
        
    	var search = $searchInput.val();
        var empty = !(search && search.length > 0);
        var found = false;
        $tree.find('span').each(function () {
            var text = $(this).text();
            if (!empty) {
            	var pieces = search.split(/[\/\.]/);
            	for (var i = 0; i < pieces.length; i++) {
	                var regex = new RegExp('(' + pieces[i] + '+)', 'gi');
	                if (regex.test(text)) {
	                    $(this).html(text.replace(regex, '<mark>$1</mark>'));
	                    $(this).parents('li').addClass('marked');
	                    $(this).parent('li').addClass('found');
	                    found = true;
	                    //return;
	                }
            	}
            	if (found) {
                	return;
            	}
            }
            $(this).html(text);
        });
    	if (found) {
            $searchInput.parent().removeClass('not-found');
        } else if (empty) {
            $searchInput.parent().removeClass('not-found');
        } else {
        	$searchInput.parent().addClass('not-found');
        }

        if ($searchCheckbox.is(':checked') && found) {
            $tree.find('.marked').slideDown();
            $tree.find('li:not(.marked)').slideUp();
        } else {
        	$('li').slideDown();
        }
    }
    $searchInput.focus().on('keyup', filterTree);
    $searchCheckbox.change(filterTree);

    $('#javadoc-tree ul span').on('click', function() { navigateTo(this.title);});
    
});
