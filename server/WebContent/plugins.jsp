<%@ page import="org.pocketcampus.core.plugin.Core;" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" lang="en-US"> 

<head profile="http://gmpg.org/xfn/11"> 
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" /> 
 
	<title>PocketCampus Server</title> 
	
	<link rel="stylesheet" href="http://mikejolley.com/wp-content/themes/minicard/style.css" type="text/css" /> 
	
				<style type="text/css"> 
					body {
						background-image: url(static/images/bg/burst.jpg);
						background-repeat: no-repeat;
					}
				</style> 
				
	<script type='text/javascript' src='http://code.jquery.com/jquery-1.4.2.min.js'></script> 
	
	<script type="text/javascript"> 
	/* <![CDATA[ */
		jQuery.noConflict();
		(function($) { 
			$(function() {
			
											
				// Main Nav Ajax Stuff
				$('#mainNav a').click(function(){
					var url = $(this).attr('href');
					
					$("#content").slideUp('',function(){
						$(this).load( url + " #content .inner", function() {
							$(this).slideDown();
						})
					});
					
					$('#mainNav li').removeClass('current_page_item current_page_parent current_page_ancestor');
					$(this).parent().addClass('current_page_item');
					return false;
				});
			});
		})(jQuery);
	/* ]]> */
	</script>	
	
</head> 
<body> 
<div id="wrapper"> 
 
	<div class="vcard" id="header"> 
	
			<h1 id="name">PocketCampus Server</h1> 
			<p class="title">EPFL Test Instance</p> 
 
		<div class="clear"></div> 
	</div> 
	<div class="clear"></div> 
	<div id="mainNav"> 
		<ul>
			<li class="page_item page-item-3"><a href="index.jsp" title="Dashboard">Dashboard</a></li> 
			<li class="page_item page-item-2 current_page_item"><a href="plugins.jsp" title="Plugins">Plugins</a></li> 
		</ul> 
		<div class="clear"></div> 
	</div> 
	<div id="content_wrapper"><div id="content"><div class="inner">
	<ul class="social">
	
		<%
		boolean isOdd = false;
		int num = 0;
		String type;
		String first;
		
		for(String pluginName : Core.getInstance().getMethodList().keySet()) {
			type = "";
			
			if(isOdd) {
				type = "alt";
			}
			
			if(num<2) {
				type += " first";
			}
			
			type = " class=\"" + type + "\"";
			
			out.print("<li"+type+"><a href=\"#\" style=\"background-image: url(static/images/plugin.png)\">"+pluginName.substring(pluginName.lastIndexOf("."))+"</a></li>");
			
			isOdd = !isOdd;
			num++;
		}
		
		if(isOdd) {
			out.print("<li class=\"alt\"></li>");
		}
		
		%>
	</ul>
	<div class="clear"></div> 
	<div class="clear"></div> 
	</div></div></div><!-- end content -->

	
	
</div><!-- end wrapper --> 
</body> 
</html>