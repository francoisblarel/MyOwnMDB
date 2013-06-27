$(function() {

    $('a[rel=tipsy]').tipsy({fade: true, gravity: 'n'});

    $("#charge").click(function(){
        window.location=Router.controllers.Application.charge().url
    })

    if (!!window.EventSource) {
        // on appelle l'action feed
        var feed = new EventSource("/feed");

        feed.addEventListener('open', function(e){
        },false);

        feed.addEventListener('message', function(e){
            var data = e.data;
            var json = $.parseJSON(data);

            $.each(json, function(key, value){
                $("#voteEvents").append("<div>"+ value +"</div>");
            });
        },false);

        feed.addEventListener('error', function(e){
            //
        },false);

    }else{
        alert("Le navigateur ne prends pas en compte les EventSource")
    }
})
