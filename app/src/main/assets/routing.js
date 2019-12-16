function mapLocation() {
    var directionsDisplay;
    var directionsService = new google.maps.DirectionsService();
    var map;

    function initialize() {
        directionsDisplay = new google.maps.DirectionsRenderer();
        var chicago = new google.maps.LatLng(37.334818, -121.884886);
        var mapOptions = {
            zoom: 7,
            center: chicago
        };
        map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
        directionsDisplay.setMap(map);
        calcRoute();
    }

    function calcRoute() {
        var start = new google.maps.LatLng(41.016998,-91.9647933);
        //var end = new google.maps.LatLng(38.334818, -181.884886);
        var end = new google.maps.LatLng(41.022375,-91.9680819);
        /*
var startMarker = new google.maps.Marker({
            position: start,
            map: map,
            draggable: true
        });
        var endMarker = new google.maps.Marker({
            position: end,
            map: map,
            draggable: true
        });
*/
        var bounds = new google.maps.LatLngBounds();
        bounds.extend(start);
        bounds.extend(end);
        map.fitBounds(bounds);
        var request = {
            origin: start,
            destination: end,
            travelMode: google.maps.TravelMode.DRIVING
        };
        directionsService.route(request, function (response, status) {
            if (status == google.maps.DirectionsStatus.OK) {
                directionsDisplay.setDirections(response);
                directionsDisplay.setMap(map);
            } else {
                alert("Directions Request from " + start.toUrlValue(6) + " to " + end.toUrlValue(6) + " failed: " + status);
            }
        });
    }

    google.maps.event.addDomListener(window, 'load', initialize);
}
mapLocation();