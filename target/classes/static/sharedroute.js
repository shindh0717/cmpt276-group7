let innerMap;
let directionsRenderer = null;

const mapElement = document.querySelector("gmp-map");


async function createSharedMap() {

    const { InfoWindow } =
        await google.maps.importLibrary("maps");

    await mapElement.innerMapReady;

    innerMap = mapElement.innerMap;


    innerMap.setOptions({
        mapTypeControl: false
    });


    displayRoute(locations);
}



async function displayRoute(locations) {

    if (!locations || locations.length < 2) {
        console.log("Not enough locations");
        return;
    }


    const {
        DirectionsService,
        DirectionsRenderer
    } = await google.maps.importLibrary("routes");


    if (directionsRenderer) {
        directionsRenderer.setMap(null);
    }


    const directionsService =
        new DirectionsService();


    directionsRenderer =
        new DirectionsRenderer({
            suppressMarkers: false,
            preserveViewport: false
        });


    directionsRenderer.setMap(innerMap);



    const origin = {
        lat: locations[0].latitude,
        lng: locations[0].longitude
    };


    const destination = {
        lat: locations[locations.length - 1].latitude,
        lng: locations[locations.length - 1].longitude
    };


    const waypoints =
        locations
        .slice(1, -1)
        .map(location => ({
            location: {
                lat: location.latitude,
                lng: location.longitude
            },
            stopover: true
        }));



    directionsService.route({

        origin: origin,

        destination: destination,

        waypoints: waypoints,

        travelMode:
            google.maps.TravelMode.DRIVING

    })
    .then(response => {

        directionsRenderer.setDirections(response);

    })
    .catch(error => {

        console.error(
            "Could not display route:",
            error
        );

    });

}



createSharedMap();