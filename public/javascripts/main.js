$(function() {

    $('a[rel=tipsy]').tipsy({fade: true, gravity: 'n'});

    $("#charge").click(function(){
        window.location=Router.controllers.Application.charge().url
    })
})
