<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="${pageContext.request.contextPath}/resource/Javascript/jquery-3.6.0.min.js"></script>
<script>
$(document).ready(function (){
    var scroll_zoom = new ScrollZoom($('#container'),5,0.5)
})
 
//The parameters are:
//
//container: The wrapper of the element to be zoomed. The script will look for the first child of the container and apply the transforms to it.
//max_scale: The maximum scale (4 = 400% zoom)
//factor: The zoom-speed (1 = +100% zoom per mouse wheel tick)
 
function ScrollZoom(container,max_scale,factor){
    var target = container.children().first()
    var size = {w:target.width(),h:target.height()}
    var pos = {x:0,y:0}
    var scale = 1
    var zoom_target = {x:0,y:0}
    var zoom_point = {x:0,y:0}
    var curr_tranform = target.css('transition')
    var last_mouse_position = { x:0, y:0 }
    var drag_started = 0
 
    target.css('transform-origin','0 0')
    target.on("mousewheel DOMMouseScroll",scrolled)
    target.on('mousemove', moved)
    target.on('mousedown', function() {
        drag_started = 1;
        target.css({'cursor':'move', 'transition': 'transform 0s'});
        /* Save mouse position */
        last_mouse_position = { x: event.pageX, y: event.pageY};
    });
 
    target.on('mouseup mouseout', function() {
        drag_started = 0;
        target.css({'cursor':'default', 'transition': curr_tranform});
    });
 
    function scrolled(e){
        var offset = container.offset()
        zoom_point.x = e.pageX - offset.left
        zoom_point.y = e.pageY - offset.top
 
        e.preventDefault();
        var delta = e.delta || e.originalEvent.wheelDelta;
        if (delta === undefined) {
          //we are on firefox
          delta = e.originalEvent.detail;
        }
        delta = Math.max(-1,Math.min(1,delta)) // cap the delta to [-1,1] for cross browser consistency
 
        // determine the point on where the slide is zoomed in
        zoom_target.x = (zoom_point.x - pos.x)/scale
        zoom_target.y = (zoom_point.y - pos.y)/scale
 
        // apply zoom
        scale += delta * factor * scale
        scale = Math.max(1,Math.min(max_scale,scale))
 
        // calculate x and y based on zoom
        pos.x = -zoom_target.x * scale + zoom_point.x
        pos.y = -zoom_target.y * scale + zoom_point.y
 
        update()
    }
 
    function moved(event){
        if(drag_started == 1) {
            var current_mouse_position = { x: event.pageX, y: event.pageY};
            var change_x = current_mouse_position.x - last_mouse_position.x;
            var change_y = current_mouse_position.y - last_mouse_position.y;
 
            /* Save mouse position */
            last_mouse_position = current_mouse_position;
            //Add the position change
            pos.x += change_x;
            pos.y += change_y;
 
        update()
        }
    }
 
    function update(){
        // Make sure the slide stays in its container area when zooming out
        if(pos.x>0)
            pos.x = 0
        if(pos.x+size.w*scale<size.w)
            pos.x = -size.w*(scale-1)
        if(pos.y>0)
            pos.y = 0
        if(pos.y+size.h*scale<size.h)
            pos.y = -size.h*(scale-1)
 
        target.css('transform','translate('+(pos.x)+'px,'+(pos.y)+'px) scale('+scale+','+scale+')')
    }
}
</script>
<style type="text/css">
#linkDiv{
  text-align:center;
}
#container{
  width:100%;
  overflow:hidden;
}
#slide{
  width:100%;
  height:100%;
  transition: transform .3s;
  text-align:center;
}
img{
  width:auto;
  height:auto;
  max-width:100%;
  pointer-events: none;
}
</style>
</head>
<body>
<div id="linkDiv">
	<a href="#" onClick="self.close();">새창 닫기</a>
</div>
<div id="container">
  	<div id="slide">
		<img src="${imagePath}" alt="image"/>
	</div>
</div>
</body>
</html>