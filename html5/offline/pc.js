Storage.prototype.setObject = function(key, value) {
	this.setItem(key, JSON.stringify(value));
}
Storage.prototype.getObject = function(key) {
	var value = this.getItem(key);
	return value && JSON.parse(value);
}


//$.mobile.loadingMessageTheme = "b";
$.mobile.loadingMessage = "Loading";
$.mobile.loadingMessageTextVisible = true;

$( document ).delegate("#dashboard", "pagebeforecreate", function() {
	console.log("page #dashboard pagebeforecreate");
});
$( document ).delegate("#dashboard", "pagecreate", function() {
	console.log("page #dashboard pagecreate");
});
$( document ).delegate("#dashboard", "pageinit", function() {
	console.log("page #dashboard pageinit");
});
$( document ).delegate("#dashboard", "pagebeforeshow", function(event, data) {
	console.log("page #dashboard pagebeforeshow");
});
$( document ).delegate("#dashboard", "pageshow", function() {
	console.log("page #dashboard pageshow");
});



// Some sample categorized data. This data is in-memory
// for demonstration purposes, but could be loaded dynamically
// via ajax.
var categoryData = {
	animals: {
		name: "Animals",
		description: "All your favorites from aardvarks to zebras.",
		items: [
		{
			name: "Pets"
		},
		{
			name: "Farm Animals"
		},
		{
			name: "Wild Animals"
		}
		]
	},
	colors: {
		name: "Colors",
		description: "Fresh colors from the magic rainbow.",
		items: [
		{
			name: "Blue"
		},
		{
			name: "Green"
		},
		{
			name: "Orange"
		},
		{
			name: "Purple"
		},
		{
			name: "Red"
		},
		{
			name: "Yellow"
		},
		{
			name: "Violet"
		}
		]
	},
	vehicles: {
		name: "Vehicles",
		description: "Everything from cars to planes.",
		items: [
		{
			name: "Cars"
		},
		{
			name: "Planes"
		},
		{
			name: "Construction"
		}
		]
	}
};

// Load the data for a specific category, based on
// the URL passed in. Generate markup for the items in the
// category, inject it into an embedded page, and then make
// that page the current active page.
function showCategory( urlObj, options )
{
	var categoryName = urlObj.hash.replace( /.*category=/, "" ),
	
	// Get the object that represents the category we
	// are interested in. Note, that at this point we could
	// instead fire off an ajax request to fetch the data, but
	// for the purposes of this sample, it's already in memory.
	category = categoryData[ categoryName ],
	
	// The pages we use to display our content are already in
	// the DOM. The id of the page we are going to write our
	// content into is specified in the hash before the '?'.
	pageSelector = urlObj.hash.replace( /\?.*$/, "" );
	
	if ( category ) {
		// Get the page we are going to dump our content into.
		var $page = $( pageSelector ),
		
		// Get the header for the page.
		$header = $page.children( ":jqmData(role=header)" ),
		
		// Get the content area element for the page.
		$content = $page.children( ":jqmData(role=content)" ),
		
		// The markup we are going to inject into the content
		// area of the page.
		markup = "<p>" + category.description + "</p><ul data-role='listview' data-inset='true'>",
		
		// The array of items for this category.
		cItems = category.items,
		
		// The number of items in the category.
		numItems = cItems.length;
		
		// Generate a list item for each item in the category
		// and add it to our markup.
		for ( var i = 0; i < numItems; i++ ) {
			markup += "<li>" + cItems[i].name + "</li>";
		}
		markup += "</ul>";
		
		// Find the h1 element in our header and inject the name of
		// the category into it.
		$header.find( "h1" ).html( category.name );
		
		// Inject the category items markup into the content element.
		$content.html( markup );
		
		// Pages are lazily enhanced. We call page() on the page
		// element to make sure it is always enhanced before we
		// attempt to enhance the listview markup we just injected.
		// Subsequent calls to page() are ignored since a page/widget
		// can only be enhanced once.
		$page.page();
		
		// Enhance the listview we just injected.
		$content.find( ":jqmData(role=listview)" ).listview();
		
		// We don't want the data-url of the page we just modified
		// to be the url that shows up in the browser's location field,
		// so set the dataUrl option to the URL for the category
		// we just loaded.
		options.dataUrl = urlObj.href;
		
		// Now call changePage() and tell it to switch to
		// the page we just modified.
		$.mobile.changePage( $page, options );
	}
}


Strings = function () {};

Strings.CONNECTION_ERROR = "Sorry an error occured, try to reload";
Strings.UPSTREAM_SERVER_DOWN = "The upstream server is down";
Strings.WRONG_CREDENTIALS = "Wrong username and/or password";
Strings.BAD_TOKEN = "Bad Token";


PocketCampus = function () {};

PocketCampus.registeredDynamicPages = new Array();

PocketCampus.registerDynamicPage = function (name, callback) {
	PocketCampus.registeredDynamicPages[name] = callback;
}

PocketCampus.showToast = function (message) {
	$( "<div class='ui-loader ui-corner-all ui-body-e ui-loader-verbose ui-loader-textonly'><span class='ui-icon ui-icon-loading'></span><h1>" + message + "</h1></div>" )
	.css({ "display": "block", "opacity": 0.96, "top": "50%" })
	.appendTo( $.mobile.pageContainer )
	.delay( 1200 )
	.fadeOut( 400, function() { $( this ).remove(); });
}


// Listen for any attempts to call changePage().
$(document).bind( "pagebeforechange", function( e, data ) {
	if ( typeof data.toPage === "string" ) {
		u = $.mobile.path.parseUrl( data.toPage );
		//matches = u.hash.match(/^[#]([^&\?]+)/);
		matches = u.hash.match(/^[#]([a-zA-Z0-9\-]+)/);
		if(matches && matches[1] && PocketCampus.registeredDynamicPages[matches[1]]) {
			PocketCampus.registeredDynamicPages[matches[1]].showPage(u, data.options);
			e.preventDefault();
		}
	}
});




function coucou() {
	//$.mobile.changePage("jqm.html#moodle", {transition: "slideup"});
	$.mobile.changePage("authentication.html", {transition: "pop", role: "dialog"});
	//$("<a href='#authentication' data-rel='dialog'></a>").click().remove();
}

function loadAuthentication() {
	
	$.get( "authentication.html", function( data ) {
		
		
		
			var content = $( data ).find( '#content' );
			$( "#result" ).empty().append( content );
	});
	//$( ...new markup that contains widgets... ).appendTo( ".ui-page" ).trigger( "create" );
}

function loadAuthentication2() {
	
	// Get the page we are going to dump our content into.
	var $page = $( "#pagediv" );
	
	// Get the header for the page.
	$header = $page.children( ":jqmData(role=header)" );
	
	// Get the content area element for the page.
	$content = $page.children( ":jqmData(role=content)" );
	
	// The markup we are going to inject into the content
	// area of the page.
	markup = "<p>Desc</p><ul data-role='listview' data-inset='true'>";
	
	
	// Generate a list item for each item in the category
	// and add it to our markup.
	markup += "<li>One</li>";
	markup += "<li>Two</li>";
	markup += "</ul>";
	
	// Find the h1 element in our header and inject the name of
	// the category into it.
	$header.find( "h1" ).html( "Title" );
	
	// Inject the category items markup into the content element.
	$content.html( markup );
	
	// Pages are lazily enhanced. We call page() on the page
	// element to make sure it is always enhanced before we
	// attempt to enhance the listview markup we just injected.
	// Subsequent calls to page() are ignored since a page/widget
	// can only be enhanced once.
	$page.page();
	
	// Enhance the listview we just injected.
	$content.find( ":jqmData(role=listview)" ).listview();
	
	// We don't want the data-url of the page we just modified
	// to be the url that shows up in the browser's location field,
	// so set the dataUrl option to the URL for the category
	// we just loaded.
	//options.dataUrl = urlObj.href;
	
	// Now call changePage() and tell it to switch to
	// the page we just modified.
	$.mobile.changePage( $page );
	
	
	
}
