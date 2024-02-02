
$(document).ready(function(){

    $(".menu li a").on("click", function() {
        $(".menu li a").removeClass("hover");
        $(this).addClass("hover");
    });

    $("#toggle ,#SearchClose").click(function() {
        $("#toggle").toggleClass("active");
        $(".SearchZoneWrap").toggleClass("active");
        $(".ValueWrap").toggleClass("move");
        $(".AddSearchBoxCont").toggleClass("move");
    });

    $(".R_SearchBtn").click(function() {
        $(".SearchValueSlider").toggleClass("active");
        $(".RadioValueWrap").toggleClass("move");
        $(".GisBtnWrap").toggleClass("move");
    });

    
});

(function($){
    $.fn.dropdown = function(){
        return this.each(function(){
        var $gnb = $(this);
        var $menu = $gnb.find(".menu");
        var $depth1 = $gnb.find(".depth1");
        var $depth2 = $gnb.find(".depth2");
        
        $gnb.mouseenter(function(){
            gnbOn();
        }).mouseleave(function(){
            gnbOff();
        });
        
        $gnb.find("a").focusin(function(){
            gnbOn();
        }).focusout(function(){
            gnbOff();
        });
            
        function gnbOn(){
            $gnb.find($depth2).stop().animate({height: "200"});
        }
        
        function gnbOff(){
            $gnb.find($depth2).stop().animate({height: "0"});
        }

        });
    }
})(jQuery);

$(function(){
$(".dropdown").dropdown();
});     

function Clock() { 
    var date = new Date(); 
    var MM = Zero(date.getMonth() + 1); 
    var DD = Zero(date.getDate()); 
    var hh = Zero(date.getHours()); 
    var mm = Zero(date.getMinutes()); 
    var ss = Zero(date.getSeconds()); 
    var Week = Weekday(); 
    Write(MM, DD, hh, mm, ss, Week);
    
    function Zero(num) { 
        return (num < 10 ? '0' + num : '' + num); 
    } 
    function Weekday() { 
        var Week = ["Sun","Mon","Tue","Wed","Thr","Fri","Sat"]; 
        var Weekday = date.getDay();
        return Week[Weekday]; 
    } 
    function Write(MM, DD, hh, mm, ss, Week) { 
        var Clockday = document.getElementById("Clockday"); 
        var Clock = document.getElementById("Clock");
        Clockday.innerText = MM + '.' + DD + '(' + Week + ')'; 
        Clock.innerText = hh + ':' + mm + ':' + ss; } 
}
setInterval(Clock, 1000);

